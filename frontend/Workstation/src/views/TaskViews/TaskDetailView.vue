<script setup lang="ts">
import { ref, watch } from 'vue'

import { useTaskStore } from '@/stores/taskStore.ts'
import { useAlertStore } from '@/stores/useAlertStore.ts'

import { Badge } from '@/components/ui/badge'
import { Input } from '@/components/ui/input'
import { Textarea } from '@/components/ui/textarea'
import {
  Accordion,
  AccordionContent,
  AccordionItem,
  AccordionTrigger,
} from '@/components/ui/accordion'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import { ScrollArea } from '@/components/ui/scroll-area'

import { PriorityLabel } from '@/documentTypes/types/Priority.ts'
import { TaskStatusLabel } from '@/documentTypes/types/TaskStatus.ts'
import type { TaskDtd } from '@/documentTypes/dtds/TaskDtd.ts'
import { useDebounceFn } from '@vueuse/core'
import CommentsAccordion from '@/components/CommentsAccordion.vue'
import type { CommentCreateDtd } from '@/documentTypes/dtds/CommentCreateDtd.ts'
import { addCommentToProcessItem, assignProcessItemToUser } from '@/services/processItemService.ts'
import { TimeUnitLabel } from '@/documentTypes/types/TimeUnit.ts'
import { addBlockingTask, addWorkingTime, updateTask } from '@/services/taskService.ts'
import UserSelect from '@/components/UserSelect.vue'
import type { DateValue } from '@internationalized/date'
import { CalendarDate, DateFormatter, getLocalTimeZone } from '@internationalized/date'
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from '@/components/ui/dialog'
import { BookCheck, CalendarIcon } from 'lucide-vue-next'

import { Popover, PopoverContent, PopoverTrigger } from '@/components/ui/popover'
import { Button } from '@/components/ui/button'
import { Calendar } from '@/components/ui/calendar'
import type { UpdateTaskDtd } from '@/documentTypes/dtds/UpdateTaskDtd.ts'
import ExpertiseSelect from '@/components/ExpertiseSelect.vue'
import { useRouter } from 'vue-router'
import type { UserDtd } from '@/documentTypes/dtds/UserDtd.ts'
import { WorkingTimeUnit, WorkingTimeUnitlabel } from '@/documentTypes/types/WorkingTimeUnit.ts'
import AttachmentList from '@/components/AttachmentList.vue'
import { useUserStore } from '@/stores/userStore.ts'
import { deletePlannedCapacity } from '@/services/capacityService.ts'
import { Role } from '@/documentTypes/types/Role.ts'
import Modal from '@/components/Modal.vue'
import TaskSelect from '@/components/TaskSelect.vue'
import { format, parseISO } from 'date-fns'

const userStore = useUserStore()
const { hasAnyRole } = userStore
const taskStore = useTaskStore()
const alertStore = useAlertStore()
const router = useRouter()

const editableTask = ref<TaskDtd | null>(null)
const commentText = ref('')
const description = ref('')
const acceptanceCriteria = ref('')
const estimatedTime = ref(0)
const workingTimeInMinutes = ref(0)
const dueDateValue = ref<DateValue>()
const showAddExpertise = ref(false)
const addingBlockedByTask = ref(false)
const selectedBlockedByTask = ref<TaskDtd | null>(null)
const assignee = ref<UserDtd | null>(null)

const ignoreNextUpdate = ref(false)

const showWorkingTimeDialog = ref(false)
const workingTimeToAdd = ref(0)
const unit = ref<WorkingTimeUnit>(WorkingTimeUnit.MINUTES)

const showAlreadyPlannedDialog = ref(false)

const dataFormatter = new DateFormatter('de-DE', {
  dateStyle: 'medium',
})

const expertiseIdToAdd = ref<number | null>(null)

watch(
  () => taskStore.selectedTask,
  (newTask) => {
    if (newTask) {
      editableTask.value = { ...newTask }
      ignoreNextUpdate.value = true
      description.value = newTask.processItem.description
      acceptanceCriteria.value = newTask.acceptanceCriteria
      estimatedTime.value = newTask.estimatedTime
      workingTimeInMinutes.value = newTask.workingTimeInMinutes ?? 0
      assignee.value = newTask.processItem.assignee

      if (newTask.dueDate) {
        const [year, month, day] = newTask.dueDate!.split('-').map(Number)
        dueDateValue.value = new CalendarDate(year, month, day)
      } else {
        dueDateValue.value = undefined
      }
    } else {
      editableTask.value = null
      description.value = ''
      acceptanceCriteria.value = ''
      estimatedTime.value = 0
      workingTimeInMinutes.value = 0
      dueDateValue.value = undefined
    }
  },
  { immediate: true, deep: true },
)

const debouncedSave = useDebounceFn(async () => {
  if (!editableTask.value) return

  await saveTask()
}, 500)

watch(
  () => [
    description.value,
    estimatedTime.value,
    acceptanceCriteria.value,
    workingTimeInMinutes.value,
  ],
  () => {
    if (ignoreNextUpdate.value) {
      ignoreNextUpdate.value = false
      return
    }
    debouncedSave()
  },
)

async function submitWorkingTime() {
  if (editableTask.value === null) return

  try {
    await addWorkingTime(editableTask.value.processItem.id, workingTimeToAdd.value, unit.value)

    alertStore.show('Arbeitszeit erfolgreich hinzugefügt', 'success')
    showWorkingTimeDialog.value = false
  } catch (error) {
    alertStore.show('Fehler beim Hinzufügen der Arbeitszeit', 'error')
  }

  workingTimeToAdd.value = 0
}

async function saveTask() {
  if (!editableTask.value) return
  try {
    const dto: UpdateTaskDtd = {
      description: description.value,
      priority: editableTask.value.priority,
      status: editableTask.value.status,
      estimatedTime: estimatedTime.value,
      estimationUnit: editableTask.value.estimationUnit,
      acceptanceCriteria: acceptanceCriteria.value,
      dueDate: dueDateValue.value ? dueDateValue.value.toString() : undefined,
      expertiseIds: expertiseIdToAdd.value ? [expertiseIdToAdd.value] : undefined,
    }
    await updateTask(editableTask.value.processItem.id, dto)
  } catch (err: any) {
    editableTask.value = { ...taskStore.selectedTask } as TaskDtd
    const msg = err.response?.data?.message || err.response?.data || err.message || String(err)
    alertStore.show('Fehler beim Speichern: ' + msg, 'error')
  }
}

function addExpertise() {
  if (!expertiseIdToAdd.value) return

  const existsInTask = editableTask.value!.expertise.some(
    (expertise) => expertise.id === expertiseIdToAdd.value,
  )
  if (existsInTask) {
    alertStore.show('Expertise ist bereits vorhanden')
    return
  }

  saveTask()
  expertiseIdToAdd.value = null

  toggleExpertise()
}

function toggleExpertise() {
  if (showAddExpertise.value) {
    expertiseIdToAdd.value = null
    showAddExpertise.value = false
  } else {
    showAddExpertise.value = true
  }
}

async function addComment() {
  if (!editableTask.value || !commentText.value) return

  if (userStore.user === null) {
    console.log('user is null')
    return
  }

  const authorId = userStore.user?.id

  const commentCreateDtd: CommentCreateDtd = {
    text: commentText.value,
    authorId: authorId,
  }

  try {
    await addCommentToProcessItem(editableTask.value.processItem.id, commentCreateDtd)

    alertStore.show('Kommentar erfolgreich erstellt', 'success')
    commentText.value = ''
  } catch (err) {
    alertStore.show('Fehler beim Kommentieren', 'error')
  }
}

function startCapacityPlanning() {
  if (!editableTask.value?.processItem.id) {
    alert('Task Id fehlt')
    return
  }

  if (editableTask.value.isAlreadyPlanned) {
    showAlreadyPlannedDialog.value = true
  } else {
    moveToCapacityPlanning()
  }
}

function moveToCapacityPlanning() {
  if (editableTask.value?.processItem.id) {
    router.push({
      name: 'capacityPlanningView',
      params: { taskId: editableTask.value.processItem.id },
    })
  } else {
    alert('Task Id fehlt')
  }
}

async function updateAssignee() {
  if (!editableTask.value) return

  const id = assignee.value?.id ?? -1

  try {
    await assignProcessItemToUser(editableTask.value.processItem.id, id)
  } catch (err: any) {
    const msg = err.response?.data?.message || err.response?.data || err.message || String(err)
    alertStore.show(`Fehler bei der Zuweisung): ${msg}`, 'error')
  }
}

async function submitRepeatPlanning() {
  if (!editableTask.value) return

  try {
    await deletePlannedCapacity(editableTask.value.processItem.id)

    alertStore.show(`Vorgenommene Kapazitäten wurden entfernt.`, 'success')
    moveToCapacityPlanning()
  } catch (err: any) {
    const msg = err.response?.data?.message || err.response?.data || err.message || String(err)
    alertStore.show(`${msg}`, 'error')
  }
}

function toggleAddBlockedByTask() {
  selectedBlockedByTask.value = null
  addingBlockedByTask.value = !addingBlockedByTask.value
}

async function addBlockedByTask() {
  if (!editableTask.value || !selectedBlockedByTask.value) return

  try {
    await addBlockingTask(
      editableTask.value.processItem.id,
      selectedBlockedByTask.value.processItem.id,
    )
    alertStore.show(`Blockierung erfolgreich erstellt`, 'success')
    toggleAddBlockedByTask()
  } catch (err: any) {
    const msg = err.response?.data?.message || err.response?.data || err.message || String(err)
    alertStore.show(`${msg}`, 'error')
  }
}

function determineFinishDate() {
  if (!editableTask.value) return
  if (!editableTask.value.calendarEntryDates || editableTask.value.calendarEntryDates.length === 0)
    return

  //must be parsed to Date object to determine latest one
  const maxTimestamp = Math.max(
    ...editableTask.value.calendarEntryDates.map((date) => new Date(date).getTime()),
  )

  return new Date(maxTimestamp)
}

function formatDate(date: string | undefined | null): string {
  if (!date) return ''
  const parsedDate = parseISO(date)
  return format(parsedDate, 'dd.MM.yyyy')
}
</script>

<template>
  <div v-if="editableTask" class="flex h-screen gap-6 p-6">
    <!-- Left Area -->
    <ScrollArea class="flex-1 overflow-auto">
      <div class="p-6 space-y-4">
        <div>
          <div class="flex justify-between">
            <h2 class="text-xl font-bold">
              {{ editableTask.processItem.id }} - {{ editableTask.processItem.title }}
            </h2>

            <div
              v-if="editableTask.isAlreadyPlanned"
              class="flex flex-col items-end space-x-2 text-right"
            >
              <BookCheck class="stroke-1" />
              <p>Voraussichtlich fertiggestellt am:</p>
              <div
                v-if="editableTask.calendarEntryDates && editableTask.calendarEntryDates.length > 0"
              >
                {{ formatDate(determineFinishDate()?.toISOString()) }}
              </div>
            </div>
          </div>
          <div class="flex gap-2 mt-2">
            <Badge v-for="expertise in editableTask.expertise" :key="expertise.id">
              {{ expertise.name }}
            </Badge>
            <Button
              v-if="hasAnyRole([Role.ADMIN, Role.PROJECT_PLANNER, Role.CAPACITY_PLANNER])"
              class="cursor-pointer"
              @click="toggleExpertise"
              >+
            </Button>
          </div>

          <div v-if="showAddExpertise" class="flex pt-3 space-x-2">
            <ExpertiseSelect v-model="expertiseIdToAdd" />
            <Button class="cursor-pointer" @click="addExpertise">Hinzufügen</Button>
            <Button class="cursor-pointer" variant="secondary" @click="toggleExpertise"
              >Abbrechen
            </Button>
          </div>

          <div class="flex gap-6 mt-4 text-sm">
            <div v-if="editableTask.requestId">
              <span class="font-semibold">Anfrage</span><br />
              <RouterLink :to="`/requests/${editableTask.requestId}`">
                {{ editableTask.requestId }} - {{ editableTask.requestTitle }}
              </RouterLink>
            </div>

            <div v-if="editableTask.projectId">
              <span class="font-semibold">Projekt</span><br />
              <RouterLink :to="`/projects/${editableTask.projectId}`">
                {{ editableTask.projectId }} - {{ editableTask.projectTitle }}
              </RouterLink>
            </div>

            <div>
              <span class="font-semibold">Geplant bis</span><br />
              <Popover>
                <PopoverTrigger as-child>
                  <Button
                    :disabled="
                      !hasAnyRole([Role.ADMIN, Role.PROJECT_PLANNER, Role.CAPACITY_PLANNER])
                    "
                    variant="outline"
                    class="cursor-pointer"
                    :class="[
                      'w-[150px] justify-start',
                      !dueDateValue ? 'text-muted-foreground' : '',
                    ]"
                  >
                    <CalendarIcon class="mr-2 h-4 w-4" />
                    {{
                      dueDateValue
                        ? dataFormatter.format(dueDateValue.toDate(getLocalTimeZone()))
                        : 'Datum wählen'
                    }}
                  </Button>
                </PopoverTrigger>
                <PopoverContent class="w-auto p-0">
                  <Calendar v-model="dueDateValue" initial-focus @update:modelValue="saveTask" />
                </PopoverContent>
              </Popover>
            </div>
          </div>
        </div>

        <Accordion type="multiple" class="w-full" collapsible :defaultValue="['desc', 'comments']">
          <AccordionItem value="desc">
            <AccordionTrigger>Beschreibung</AccordionTrigger>
            <AccordionContent>
              <Textarea v-model="description" class="mt-2 min-h-[200px] resize-none" />
            </AccordionContent>
          </AccordionItem>

          <AccordionItem value="acceptance">
            <AccordionTrigger>Akzeptanzkriterien</AccordionTrigger>
            <AccordionContent>
              <Textarea v-model="acceptanceCriteria" class="mt-2 min-h-[130px] resize-none" />
            </AccordionContent>
          </AccordionItem>

          <AccordionItem value="blockingTasks">
            <AccordionTrigger>Blockierende Aufgaben</AccordionTrigger>
            <AccordionContent class="flex flex-col gap-2">
              <div v-if="editableTask.blockedBy.length > 0" class="text-xs">Ist blockiert von:</div>
              <div v-for="task in editableTask.blockedBy" :key="task.id">
                <RouterLink :to="`/tasks/${task.id}`" class="block">
                  <div
                    class="flex items-center justify-between border p-2 rounded cursor-pointer hover:bg-accent/20"
                  >
                    <div class="flex items-center gap-2">
                      <span>{{ task.id }}</span>
                      <span class="font-semibold">{{ task.title }}</span>
                    </div>
                    <Badge variant="secondary">{{ task.status }}</Badge>
                  </div>
                </RouterLink>
              </div>

              <div v-if="editableTask.blocks.length > 0" class="text-xs">
                Diese Aufgabe blockiert:
              </div>
              <div v-for="task in editableTask.blocks" :key="task.id">
                <RouterLink :to="`/tasks/${task.id}`" class="block">
                  <div
                    class="flex items-center justify-between border p-2 rounded cursor-pointer hover:bg-accent/20"
                  >
                    <div class="flex items-center gap-2">
                      <span>{{ task.id }}</span>
                      <span class="font-semibold">{{ task.title }}</span>
                    </div>
                    <Badge variant="secondary">{{ task.status }}</Badge>
                  </div>
                </RouterLink>
              </div>

              <div v-if="addingBlockedByTask" class="flex gap-2 items-center pt-2">
                <TaskSelect v-model="selectedBlockedByTask" />

                <Button class="cursor-pointer" @click="addBlockedByTask">Erstellen</Button>
                <Button class="cursor-pointer" variant="ghost" @click="toggleAddBlockedByTask">
                  Abbrechen
                </Button>
              </div>

              <div class="flex justify-end">
                <Button
                  v-if="hasAnyRole([Role.ADMIN, Role.PROJECT_PLANNER, Role.CAPACITY_PLANNER])"
                  class="cursor-pointer"
                  @click="toggleAddBlockedByTask"
                  >+
                </Button>
              </div>
            </AccordionContent>
          </AccordionItem>

          <AccordionItem value="attachment">
            <AccordionTrigger>Anhänge</AccordionTrigger>
            <AccordionContent>
              <AttachmentList
                :attachments="editableTask.processItem.attachments"
                :processItemId="editableTask.processItem.id"
              />
            </AccordionContent>
          </AccordionItem>

          <CommentsAccordion
            v-model="commentText"
            :comments="editableTask.processItem.comments"
            @submit="addComment"
          />
        </Accordion>
      </div>
      <Button
        v-if="hasAnyRole([Role.ADMIN, Role.CAPACITY_PLANNER])"
        class="cursor-pointer"
        @click="startCapacityPlanning"
        >Zur Planung
      </Button>
    </ScrollArea>

    <!-- right sidebar -->
    <div class="w-[200px] space-y-4 p-4 border-l-2 border-accent-200 h-screen">
      <div>
        <label class="text-sm font-semibold">Priorität</label>
        <Select v-model="editableTask.priority" @update:modelValue="saveTask">
          <SelectTrigger
            :disabled="
              !hasAnyRole([
                Role.ADMIN,
                Role.PROJECT_PLANNER,
                Role.CAPACITY_PLANNER,
                Role.TASK_REVISER,
              ])
            "
          >
            <SelectValue placeholder="Select..." />
          </SelectTrigger>
          <SelectContent>
            <SelectItem
              v-for="[value, prioLabel] in Object.entries(PriorityLabel)"
              :key="value"
              :value="value"
            >
              {{ prioLabel }}
            </SelectItem>
          </SelectContent>
        </Select>
      </div>

      <div>
        <label class="text-sm font-semibold">Status</label>
        <Select v-model="editableTask.status" @update:modelValue="saveTask">
          <SelectTrigger
            :disabled="
              !hasAnyRole([
                Role.ADMIN,
                Role.PROJECT_PLANNER,
                Role.CAPACITY_PLANNER,
                Role.TASK_REVISER,
              ])
            "
          >
            <SelectValue placeholder="Offen" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem
              v-for="[value, taskStatusLabel] in Object.entries(TaskStatusLabel)"
              :key="value"
              :value="value"
            >
              {{ taskStatusLabel }}
            </SelectItem>
          </SelectContent>
        </Select>
      </div>

      <UserSelect
        v-model="assignee"
        :disabled="editableTask.isAlreadyPlanned"
        @update:modelValue="updateAssignee"
      />

      <div>
        <label class="text-sm font-semibold">Geschätzte Zeit</label>

        <div class="flex space-x-2 border-b border-gray-300 pb-2 mb-2">
          <Input
            :disabled="
              !hasAnyRole([
                Role.ADMIN,
                Role.PROJECT_PLANNER,
                Role.CAPACITY_PLANNER,
                Role.TASK_REVISER,
              ])
            "
            type="number"
            v-model="estimatedTime"
            placeholder="Schätzung in Minuten"
          />

          <Select v-model="editableTask.estimationUnit" @update:modelValue="saveTask">
            <SelectTrigger
              :disabled="
                !hasAnyRole([
                  Role.ADMIN,
                  Role.PROJECT_PLANNER,
                  Role.CAPACITY_PLANNER,
                  Role.TASK_REVISER,
                ])
              "
            >
              <SelectValue placeholder="Zeiteinheit" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem
                v-for="[value, timeUnitLabel] in Object.entries(TimeUnitLabel)"
                :key="value"
                :value="value"
              >
                {{ timeUnitLabel }}
              </SelectItem>
            </SelectContent>
          </Select>
        </div>
      </div>

      <div>
        <label class="text-sm font-semibold">Aufgewendete Zeit (min)</label>
        <div class="flex space-x-2">
          <Input type="number" v-model="workingTimeInMinutes" disabled />

          <Dialog
            v-if="hasAnyRole([Role.ADMIN, Role.TASK_REVISER])"
            v-model:open="showWorkingTimeDialog"
          >
            <DialogTrigger as-child>
              <Button class="cursor-pointer">+</Button>
            </DialogTrigger>

            <DialogContent>
              <DialogHeader>
                <DialogTitle>Arbeitszeit hinzufügen</DialogTitle>
              </DialogHeader>
              <div class="space-y-4">
                <Input
                  type="number"
                  v-model="workingTimeToAdd"
                  placeholder="Arbeitszeit eingeben"
                  class="w-full"
                />
                <Select v-model="unit" class="w-full">
                  <SelectTrigger>
                    <SelectValue placeholder="Einheit wählen" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem
                      v-for="[value, workingTimeUnitLabel] in Object.entries(WorkingTimeUnitlabel)"
                      :key="value"
                      :value="value"
                    >
                      {{ workingTimeUnitLabel }}
                    </SelectItem>
                  </SelectContent>
                </Select>
                <div class="flex justify-end space-x-2">
                  <Button
                    class="cursor-pointer"
                    variant="secondary"
                    @click="showWorkingTimeDialog = false"
                    >Abbrechen
                  </Button>
                  <Button class="cursor-pointer" @click="submitWorkingTime">Hinzufügen</Button>
                </div>
              </div>
            </DialogContent>
          </Dialog>
        </div>

        <Modal
          title="Aufgabe bereits verplant."
          variant="warning"
          :show="showAlreadyPlannedDialog"
          :message="`Diese Aufgabe ist bereits für  ${editableTask.processItem.assignee?.name} verplant. Soll die Planung erneut durchgeführt werden?
                    Die bereits vorhandene Planung wird in diesem Fall gelöscht.`"
          @_continue="submitRepeatPlanning"
          @abort="showAlreadyPlannedDialog = false"
        />
      </div>
    </div>
  </div>
</template>

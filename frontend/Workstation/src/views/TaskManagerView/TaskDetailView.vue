<script setup lang="ts">
import {ref, watch} from 'vue'

import {useTaskStore} from '@/stores/taskStore.ts'
import {useAlertStore} from '@/stores/useAlertStore.ts'

import {Badge} from '@/components/ui/badge'
import {Input} from '@/components/ui/input'
import {Textarea} from '@/components/ui/textarea'
import {
  Accordion,
  AccordionContent,
  AccordionItem,
  AccordionTrigger,
} from '@/components/ui/accordion'
import {
  Select,
  SelectTrigger,
  SelectValue,
  SelectContent,
  SelectItem,
} from '@/components/ui/select'
import {ScrollArea} from '@/components/ui/scroll-area'

import {PriorityLabel} from '@/documentTypes/types/Priority.ts'
import {TaskStatusLabel} from '@/documentTypes/types/TaskStatus.ts'
import type {TaskDtd} from '@/documentTypes/dtds/TaskDtd.ts'
import {useDebounceFn} from '@vueuse/core'
import CommentsAccordion from '@/components/CommentsAccordion.vue'
import type {CommentCreateDtd} from '@/documentTypes/dtds/CommentCreateDtd.ts'
import {addCommentToProcessItem} from '@/services/commentService.ts'
import {TimeUnitLabel} from '@/documentTypes/types/TimeUnit.ts'
import {updateTask} from '@/services/taskService.ts'
import UserSelect from '@/components/UserSelect.vue'
import type {DateValue} from "@internationalized/date";
import {
  DateFormatter,
  getLocalTimeZone,
  CalendarDate,
} from "@internationalized/date";
import {CalendarIcon} from "lucide-vue-next";

import {Popover, PopoverContent, PopoverTrigger} from "@/components/ui/popover";
import {Button} from "@/components/ui/button";
import {Calendar} from "@/components/ui/calendar";
import type {UpdateTaskDtd} from "@/documentTypes/dtds/UpdateTaskDtd.ts";
import ExpertiseSelect from "@/components/ExpertiseSelect.vue";
import {useRouter} from "vue-router";

const taskStore = useTaskStore()
const alertStore = useAlertStore()
const router = useRouter()

const editableTask = ref<TaskDtd | null>(null)
const commentText = ref('')
const description = ref('')
const acceptanceCriteria = ref('')
const estimatedTime = ref(0)
const workingTimeInMinutes = ref(0)
const dueDateValue = ref<DateValue>();
const showAddExpertise = ref(false)

const ignoreNextUpdate = ref(false)

const dataFormatter = new DateFormatter("de-DE", {
  dateStyle: "medium",
});

const expertiseIdToAdd = ref<number | null>(null)

watch(
  () => taskStore.selectedTask,
  (newTask) => {
    if (newTask) {
      editableTask.value = {...newTask}
      ignoreNextUpdate.value = true
      description.value = newTask.processItem.description
      acceptanceCriteria.value = newTask.acceptanceCriteria
      estimatedTime.value = newTask.estimatedTime
      workingTimeInMinutes.value = newTask.workingTimeInMinutes ?? 0

      const [year, month, day] = newTask.dueDate!.split("-").map(Number);
      dueDateValue.value = new CalendarDate(year, month, day);
    } else {
      editableTask.value = null
      description.value = ''
      acceptanceCriteria.value = ''
      estimatedTime.value = 0
      workingTimeInMinutes.value = 0
      dueDateValue.value = undefined;
    }
  },
  {immediate: true, deep: true},
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

async function saveTask() {
  if (!editableTask.value) return
  try {
    const dto: UpdateTaskDtd = {
      description: description.value,
      priority: editableTask.value.priority,
      status: editableTask.value.status,
      assigneeId: editableTask.value.processItem.assignee.id,
      estimatedTime: estimatedTime.value,
      acceptanceCriteria: acceptanceCriteria.value,
      workingTimeInMinutes: workingTimeInMinutes.value,
      dueDate: dueDateValue.value ? dueDateValue.value.toString() : undefined,
      expertiseIds: expertiseIdToAdd.value ? [expertiseIdToAdd.value] : undefined
    }
    await updateTask(editableTask.value.processItem.id, dto)
  } catch (err: any) {
    const msg = err.response?.data?.message || err.response?.data || err.message || String(err)
    alertStore.show('Fehler beim Speichern: ' + msg, 'error')
  }
}


function addExpertise() {
  if (!expertiseIdToAdd.value) return

  const existsInTask = editableTask.value!.expertise.some(
    (expertise) => expertise.id === expertiseIdToAdd.value
  )
  if (existsInTask) {
    alertStore.show('Expertise ist bereits vorhanden')
    return
  }

  saveTask()
  expertiseIdToAdd.value = null
}

function switchShowExpertise() {
  if (showAddExpertise.value) {
    expertiseIdToAdd.value = null
    showAddExpertise.value = false
  } else {
    showAddExpertise.value = true
  }
}


async function addComment() {
  if (!editableTask.value || !commentText.value) return

  const commentCreateDtd: CommentCreateDtd = {
    text: commentText.value,
    //TODO add authorId from logged in user
    authorId: 1,
  }

  try {
    await addCommentToProcessItem(editableTask.value.processItem.id, commentCreateDtd)

    alertStore.show('Kommentar erfolgreich erstellt', 'success')
    commentText.value = ''
  } catch (err) {
    alertStore.show('Fehler beim Kommentieren', 'error')
  }
}

function moveToCapacityPlanning() {
  if (editableTask.value?.processItem.id) {
    router.push({ name: 'capacityPlanningView', params: { taskId: editableTask.value.processItem.id } })
  } else {
    alert('Task Id fehlt')
  }
}

</script>

<template>
  <div v-if="editableTask" class="flex h-screen gap-6 p-6">
    <!-- Left Area -->
    <ScrollArea class="flex-1 overflow-auto">
      <div class="p-6 space-y-4">
        <div>
          <h2 class="text-xl font-bold">
            {{ editableTask.processItem.id }} - {{ editableTask.processItem.title }}
          </h2>
          <div class="flex gap-2 mt-2">
            <Badge v-for="expertise in editableTask.expertise" :key="expertise.id">
              {{ expertise.name }}
            </Badge>
            <Button @click="switchShowExpertise">+</Button>
          </div>

          <div v-if="showAddExpertise" class="flex pt-3 space-x-2">
            <ExpertiseSelect v-model="expertiseIdToAdd"/>
            <Button @click="addExpertise">Hinzufügen</Button>
            <Button variant="secondary" @click="switchShowExpertise">Abbrechen</Button>
          </div>
          <div class="flex gap-6 mt-4 text-sm">
            <div v-if="editableTask.requestId">
              <span class="font-semibold">Anfrage</span><br/>
              <RouterLink :to="`/requests/${editableTask.requestId}`">
                {{ editableTask.requestId }} - {{ editableTask.requestTitle }}
              </RouterLink>
            </div>

            <div v-if="editableTask.projectId">
              <span class="font-semibold">Projekt</span><br/>
              <RouterLink :to="`/projects/requests/${editableTask.projectId}`">
                {{ editableTask.projectId }} - {{ editableTask.projectTitle }}
              </RouterLink>
            </div>
            <div>
              <span class="font-semibold">Geplant bis</span><br/>
              <Popover>
                <PopoverTrigger as-child>
                  <Button
                    variant="outline"
                    :class="[
                      'w-[150px] justify-start',
                      !dueDateValue ? 'text-muted-foreground' : ''
                    ]"
                  >
                    <CalendarIcon class="mr-2 h-4 w-4"/>
                    {{
                      dueDateValue ? dataFormatter.format(dueDateValue.toDate(getLocalTimeZone())) : "Datum wählen"
                    }}
                  </Button>
                </PopoverTrigger>
                <PopoverContent class="w-auto p-0">
                  <Calendar v-model="dueDateValue" initial-focus @update:modelValue="saveTask"/>
                </PopoverContent>
              </Popover>
            </div>
          </div>
        </div>

        <Accordion type="multiple" class="w-full" collapsible :defaultValue="['desc', 'comments']">
          <AccordionItem value="desc">
            <AccordionTrigger>Beschreibung</AccordionTrigger>
            <AccordionContent>
              <Textarea v-model="description" class="mt-2 min-h-[200px] resize-none"/>
            </AccordionContent>
          </AccordionItem>

          <AccordionItem value="acceptance">
            <AccordionTrigger>Akzeptanzkriterien</AccordionTrigger>
            <AccordionContent>
              <Textarea v-model="acceptanceCriteria" class="mt-2 min-h-[130px] resize-none"/>
            </AccordionContent>
          </AccordionItem>

          <CommentsAccordion
            v-model="commentText"
            :comments="editableTask.processItem.comments"
            @submit="addComment"
          />
        </Accordion>
      </div>
      <Button @click="moveToCapacityPlanning">Zur Planung</Button>
    </ScrollArea>

    <!-- right sidebar -->
    <div class="w-[200px] space-y-4 p-4 border-l-2 border-accent-200 h-screen">
      <div>
        <label class="text-sm font-semibold">Priorität</label>
        <Select v-model="editableTask.priority" @update:modelValue="saveTask">
          <SelectTrigger>
            <SelectValue placeholder="Select..."/>
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
          <SelectTrigger>
            <SelectValue placeholder="Offen"/>
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

      <UserSelect v-model="editableTask.processItem.assignee" @update:modelValue="saveTask"/>

      <div>
        <label class="text-sm font-semibold">Geschätzte Zeit</label>
        <Input type="number" v-model="estimatedTime" placeholder="Schätzung in Minuten"/>
      </div>

      <div class="border-b border-gray-300 pb-2 mb-2">
        <label class="text-sm font-semibold">Einheit</label>
        <Select v-model="editableTask.estimationUnit" @update:modelValue="saveTask">
          <SelectTrigger>
            <SelectValue placeholder="Zeiteinheit"/>
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

      <div>
        <label class="text-sm font-semibold">Aufgewandte Zeit (min)</label>
        <Input
          type="number"
          v-model="workingTimeInMinutes"
          placeholder="Zeit eintragen"
          @update:modelValue="saveTask"
        />
      </div>
    </div>
  </div>
</template>

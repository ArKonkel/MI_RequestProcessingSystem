<script setup lang="ts">
import {ref, watch} from 'vue'
import {useProjectStore} from '@/stores/projectStore.ts'
import {useAlertStore} from '@/stores/useAlertStore.ts'
import {useDebounceFn} from '@vueuse/core'
import {ScrollArea} from '@/components/ui/scroll-area'
import {Badge} from '@/components/ui/badge'
import {Textarea} from '@/components/ui/textarea'
import {Label} from '@/components/ui/label'
import {
  Accordion,
  AccordionContent,
  AccordionItem,
  AccordionTrigger,
} from '@/components/ui/accordion'
import {Select, SelectContent, SelectItem, SelectTrigger, SelectValue} from '@/components/ui/select'
import {Button} from '@/components/ui/button'
import {Popover, PopoverTrigger, PopoverContent} from '@/components/ui/popover'
import {Calendar} from '@/components/ui/calendar'
import {CalendarIcon} from 'lucide-vue-next'
import type {DateValue} from '@internationalized/date'
import {getLocalTimeZone, parseDate, DateFormatter, CalendarDate} from '@internationalized/date'
import {useRouter} from 'vue-router'

import type {ProjectDtd} from '@/documentTypes/dtds/ProjectDtd.ts'
import {ProjectStatusLabel} from '@/documentTypes/types/ProjectStatus.ts'
import UserSelect from "@/components/UserSelect.vue";
import CommentsAccordion from "@/components/CommentsAccordion.vue";
import {Input} from "@/components/ui/input";
import type {CommentCreateDtd} from "@/documentTypes/dtds/CommentCreateDtd.ts";
import {addCommentToProcessItem} from "@/services/commentService.ts";
import type {TaskCreateDtd} from "@/documentTypes/dtds/TaskCreateDtd.ts";
import {createTask} from "@/services/taskService.ts";
import {
  ProjectDependencyType,
  ProjectDependencyTypeLabel
} from "@/documentTypes/types/ProjectDependencyType.ts";
import type {ProjectUpdateDtd} from "@/documentTypes/dtds/ProjectUpdateDtd.ts";
import {createProjectDependency, updateProject} from "@/services/projectService.ts";
import ProjectSelect from "@/components/ProjectSelect.vue";
import type {CreateDependencyDtd} from "@/documentTypes/dtds/CreateDependencyDtd.ts";

const projectStore = useProjectStore()
const alertStore = useAlertStore()
const router = useRouter()

const editableProject = ref<ProjectDtd | null>(null)
const description = ref('')
const startDateValue = ref<DateValue>()
const endDateValue = ref<DateValue>()
const ignoreNextUpdate = ref(false)

const commentText = ref('')
const addingTask = ref(false)
const newTaskTitle = ref('')

const addingDependency = ref(false)
const selectedDependency = ref<ProjectDependencyType | null>(null)
const selectedDependencyProject = ref<ProjectDtd | null>(null)

const dataFormatter = new DateFormatter('de-DE', {
  dateStyle: 'medium',
})

// Watch store
watch(
  () => projectStore.selectedProjects,
  (newProj) => {
    if (newProj) {
      editableProject.value = {...newProj}
      ignoreNextUpdate.value = true
      description.value = newProj.processItem.description

      if (newProj.startDate) {
        const [yearStart, monthStart, dayStart] = newProj.startDate!.split('-').map(Number)
        startDateValue.value = new CalendarDate(yearStart, monthStart, dayStart)
      } else {
        startDateValue.value = undefined
      }

      if (newProj.endDate) {
        const [yearEnd, monthEnd, dayEnd] = newProj.endDate!.split('-').map(Number)
        endDateValue.value = new CalendarDate(yearEnd, monthEnd, dayEnd)
      } else {
        endDateValue.value = undefined
      }
    } else {
      editableProject.value = null
      description.value = ''
      startDateValue.value = undefined
      endDateValue.value = undefined
    }
  },
  {immediate: true},
)

// debounce save
const debouncedSave = useDebounceFn(async () => {
  if (!editableProject.value) return
  await saveProject()
}, 500)

watch(
  () => description.value,
  () => {
    if (ignoreNextUpdate.value) {
      ignoreNextUpdate.value = false
      return
    }
    debouncedSave()
  },
)

// save changes
async function saveProject() {
  if (!editableProject.value) return
  try {
    const dto: ProjectUpdateDtd = {
      description: description.value,
      status: editableProject.value.status,
      assigneeId: editableProject.value.processItem.assignee?.id,
      startDate: startDateValue.value ? startDateValue.value.toString() : undefined,
      endDate: endDateValue.value ? endDateValue.value.toString() : undefined,
    }
    await updateProject(editableProject.value.processItem.id, dto)
  } catch (err: any) {
    editableProject.value = projectStore.selectedProjects
    const msg = err.response?.data?.message || err.response?.data || err.message || String(err)
    alertStore.show('Fehler beim Speichern: ' + msg, 'error')
  }
}

async function addComment() {
  if (!editableProject.value || !commentText.value) return
  const commentCreateDtd: CommentCreateDtd = {
    text: commentText.value,
    authorId: 1, // TODO: aktueller User
  }
  try {
    await addCommentToProcessItem(editableProject.value.processItem.id, commentCreateDtd)
    alertStore.show('Kommentar erfolgreich erstellt', 'success')
    commentText.value = ''
  } catch (err: any) {
    console.error(err)
    alertStore.show(err.response?.data || 'Unbekannter Fehler', 'error')
  }
}

async function addTaskToProject() {
  if (!newTaskTitle.value) return
  try {
    const dtd: TaskCreateDtd = {
      title: newTaskTitle.value,
      projectId: editableProject.value?.processItem.id,
    }
    await createTask(dtd)
    alertStore.show('Aufgabe erfolgreich erstellt', 'success')
    newTaskTitle.value = ''
    addingTask.value = false
  } catch (err: any) {
    console.error(err)
    alertStore.show(err.response?.data || 'Fehler beim Erstellen der Aufgabe', 'error')
  }
}

function showAddingTask() {
  addingTask.value = true
}

function cancelTask() {
  newTaskTitle.value = ''
  addingTask.value = false
}

async function addDependencyToProject() {
  if (!(editableProject.value && selectedDependencyProject.value && selectedDependency.value))
    return

  try {
    const createDto: CreateDependencyDtd = {
      sourceProjectId: editableProject.value?.processItem.id,
      targetProjectId: selectedDependencyProject.value?.processItem.id,
      type: selectedDependency.value
    }

    await createProjectDependency(createDto)

    alertStore.show('Abhängigkeit erfolgreich erstellt', 'success')
    editableProject.value = null
    selectedDependencyProject.value = null
    selectedDependency.value = null

  } catch (err: any) {
    console.error(err)
    alertStore.show(err.response?.data || 'Fehler beim Erstellen der Aufgabe', 'error')
  }
}

function showAddingDependency() {
  addingDependency.value = true
}

function cancelDependency() {
  addingDependency.value = false
  selectedDependency.value = null
}

// navigation zu request
function openRequest(reqId: number) {
  router.push({name: 'requestDetailView', params: {requestId: reqId}})
}
</script>

<template>
  <div v-if="editableProject" class="flex h-screen gap-6 p-6">
    <!-- Left Area -->
    <ScrollArea class="flex-1 overflow-auto">
      <div class="p-6 space-y-4">
        <div>
          <h2 class="text-xl font-bold">
            {{ editableProject.processItem.id }} - {{ editableProject.processItem.title }}
          </h2>

          <div class="flex gap-6 mt-4 text-sm">
            <div v-if="editableProject.requestId">
              <span class="font-semibold">Anfrage</span><br/>
              <RouterLink :to="`/requests/${editableProject.requestId}`">
                {{ editableProject.requestId }} - {{ editableProject.requestTitle }}
              </RouterLink>
            </div>
            <!-- End -->
            <div>
              <span class="font-semibold">Start: </span><br/>
              <Popover>
                <PopoverTrigger as-child>
                  <Button
                    variant="outline"
                    :class="[
                      'w-[150px] justify-start',
                      !startDateValue ? 'text-muted-foreground' : '',
                    ]"
                  >
                    <CalendarIcon class="mr-2 h-4 w-4"/>
                    {{
                      startDateValue
                        ? dataFormatter.format(startDateValue.toDate(getLocalTimeZone()))
                        : 'Datum wählen'
                    }}
                  </Button>
                </PopoverTrigger>
                <PopoverContent class="w-auto p-0">
                  <Calendar v-model="startDateValue" initial-focus
                            @update:modelValue="saveProject"/>
                </PopoverContent>
              </Popover>
            </div>

            <!-- End -->
            <div>
              <span class="font-semibold">Ende: </span><br/>
              <Popover>
                <PopoverTrigger as-child>
                  <Button
                    variant="outline"
                    :class="[
                      'w-[150px] justify-start',
                      !endDateValue ? 'text-muted-foreground' : '',
                    ]"
                  >
                    <CalendarIcon class="mr-2 h-4 w-4"/>
                    {{
                      endDateValue
                        ? dataFormatter.format(endDateValue.toDate(getLocalTimeZone()))
                        : 'Datum wählen'
                    }}
                  </Button>
                </PopoverTrigger>
                <PopoverContent class="w-auto p-0">
                  <Calendar v-model="endDateValue" initial-focus @update:modelValue="saveProject"/>
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

          <AccordionItem value="tasks">
            <AccordionTrigger>Aufgaben</AccordionTrigger>
            <AccordionContent class="flex flex-col gap-2">
              <div v-for="task in editableProject.tasks" :key="task.processItem.id">
                <RouterLink :to="`/tasks/${task.processItem.id}`" class="block">
                  <div
                    class="flex items-center justify-between border p-2 rounded cursor-pointer hover:bg-accent/20">
                    <div class="flex items-center gap-2">
                      <span>{{ task.processItem.id }}</span>
                      <span class="font-semibold">{{ task.processItem.title }}</span>
                    </div>
                    <Badge variant="secondary">{{ task.status }}</Badge>
                  </div>
                </RouterLink>
              </div>

              <div v-if="addingTask" class="flex gap-2 items-center">
                <Input v-model="newTaskTitle" placeholder="Titel der neuen Aufgabe" class="flex-1"/>
                <Button @click="addTaskToProject">Erstellen</Button>
                <Button variant="ghost" @click="cancelTask">Abbrechen</Button>
              </div>

              <div class="flex justify-end">
                <Button @click="showAddingTask">+</Button>
              </div>
            </AccordionContent>
          </AccordionItem>

          <AccordionItem value="deps">
            <AccordionTrigger>Abhängigkeiten</AccordionTrigger>
            <AccordionContent class="space-y-4">
              <div>
                <Label class="text-xs">Abhängig von folgenden Projekten:</Label>
                <div v-for="dep in editableProject.incomingDependencies" :key="dep.sourceProjectId">
                  <span class="font-semibold"> {{ ProjectDependencyTypeLabel[dep.type] }}: </span>
                  {{ dep.sourceProjectId }} - {{ dep.sourceProjectTitle }}
                </div>

                <div v-if="editableProject.incomingDependencies.length == 0">
                  -
                </div>
              </div>

              <div class="space-y-4">
                <Label class="text-xs">Abhängigkeiten zu folgenden Projekten:</Label>
                <div v-for="dep in editableProject.outgoingDependencies" :key="dep.targetProjectId">
                  <span class="font-semibold"> {{ ProjectDependencyTypeLabel[dep.type] }}: </span>
                  {{ dep.targetProjectId }} - {{ dep.targetProjectTitle }}
                </div>
                <div v-if="editableProject.outgoingDependencies.length == 0">
                  -
                </div>


                <div v-if="addingDependency" class="flex gap-2 items-center">
                  <Select v-model="selectedDependency">
                    <SelectTrigger>
                      <SelectValue placeholder="Select..."/>
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem
                        v-for="[value, depLabel] in Object.entries(ProjectDependencyTypeLabel)"
                        :key="value"
                        :value="value"
                      >
                        {{ depLabel }}
                      </SelectItem>
                    </SelectContent>
                  </Select>

                  <ProjectSelect v-model="selectedDependencyProject"/>

                  <Button @click="addDependencyToProject">Erstellen</Button>
                  <Button variant="ghost" @click="cancelDependency">Abbrechen</Button>
                </div>
              </div>
              <div class="flex justify-end">
                <Button @click="showAddingDependency">+</Button>
              </div>
            </AccordionContent>
          </AccordionItem>

          <CommentsAccordion
            v-model="commentText"
            :comments="editableProject.processItem.comments"
            @submit="addComment"
          />
        </Accordion>
      </div>
    </ScrollArea>

    <!-- right sidebar -->
    <div class="w-[200px] space-y-4 p-4 border-l-2 border-accent-200 h-screen">
      <div>
        <label class="text-sm font-semibold">Status</label>
        <Select v-model="editableProject.status" @update:modelValue="saveProject">
          <SelectTrigger>
            <SelectValue placeholder="Status wählen"/>
          </SelectTrigger>
          <SelectContent>
            <SelectItem
              v-for="[value, statusLabel] in Object.entries(ProjectStatusLabel)"
              :key="value"
              :value="value"
            >
              {{ statusLabel }}
            </SelectItem>
          </SelectContent>
        </Select>
      </div>

      <UserSelect v-model="editableProject.processItem.assignee" @update:modelValue="saveProject"/>
    </div>
  </div>
</template>

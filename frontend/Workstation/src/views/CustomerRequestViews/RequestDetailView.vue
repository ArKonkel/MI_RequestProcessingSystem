<script setup lang="ts">
import { ref, watch } from 'vue'

import { useRequestStore } from '@/stores/requestStore.ts'
import { useAlertStore } from '@/stores/useAlertStore.ts'
import { addCommentToProcessItem, assignProcessItemToUser } from '@/services/processItemService.ts'
import { updateCustomerRequest } from '@/services/customerRequestService.ts'

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
  SelectTrigger,
  SelectValue,
  SelectContent,
  SelectItem,
} from '@/components/ui/select'
import { ScrollArea } from '@/components/ui/scroll-area'

import { CategoryLabel } from '@/documentTypes/types/Category.ts'
import { PriorityLabel } from '@/documentTypes/types/Priority.ts'
import { RequestStatusLabel } from '@/documentTypes/types/RequestStatus.ts'

import type { RequestDtd } from '@/documentTypes/dtds/RequestDtd.ts'
import type { CommentCreateDtd } from '@/documentTypes/dtds/CommentCreateDtd.ts'
import { useDebounceFn } from '@vueuse/core'
import { TimeUnitLabel } from '@/documentTypes/types/TimeUnit.ts'
import UserSelect from '@/components/UserSelect.vue'
import CommentsAccordion from '@/components/CommentsAccordion.vue'
import { ChargeableLabel } from '@/documentTypes/types/Chargeable.ts'
import { Button } from '@/components/ui/button'
import type { TaskCreateDtd } from '@/documentTypes/dtds/TaskCreateDtd.ts'
import { createTask } from '@/services/taskService.ts'
import type { ProjectCreateDtd } from '@/documentTypes/dtds/ProjectCreateDtd.ts'
import { createProject } from '@/services/projectService.ts'
import type { UpdateCustomerRequestDtd } from '@/documentTypes/dtds/UpdateCustomerRequestDtd.ts'
import type { UserDtd } from '@/documentTypes/dtds/UserDtd.ts'
import { ProjectStatusLabel } from '@/documentTypes/types/ProjectStatus.ts'
import AttachmentList from '@/components/AttachmentList.vue'

const requestStore = useRequestStore()
const alertStore = useAlertStore()

const editableRequest = ref<RequestDtd | null>(null)
const commentText = ref('')
const description = ref('')
const estimatedScope = ref(0)

const addingTask = ref(false)
const newTaskTitle = ref('')

const assignee = ref<UserDtd | null>(null)

const addingProject = ref(false)
const newProjectTitle = ref('')

const ignoreNextUpdate = ref(false) //Need to not trigger save on switching between requests

// Watch the store for changes
watch(
  () => requestStore.selectedRequest,
  (newReq) => {
    if (newReq) {
      // make local copy
      editableRequest.value = { ...newReq }
      ignoreNextUpdate.value = true
      description.value = newReq.processItem.description
      estimatedScope.value = newReq.estimatedScope
      assignee.value = newReq.processItem.assignee
    } else {
      editableRequest.value = null
      description.value = ''
      estimatedScope.value = 0
    }
  },
  { immediate: true, deep: true },
)

//Debounce to not trigger save on every keystroke
const debouncedSave = useDebounceFn(async () => {
  if (!editableRequest.value) return

  await saveRequest()
}, 500)

watch(
  () => [description.value, estimatedScope.value],
  () => {
    if (ignoreNextUpdate.value) {
      ignoreNextUpdate.value = false //skip first update
      return
    }
    debouncedSave()
  },
)

async function addTaskToRequest() {
  if (!newTaskTitle.value) return

  try {
    const dtd: TaskCreateDtd = {
      title: newTaskTitle.value,
      requestId: editableRequest.value?.processItem.id,
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

async function addProjectToRequest() {
  if (!newProjectTitle.value) return

  try {
    const dtd: ProjectCreateDtd = {
      title: newProjectTitle.value,
      requestId: editableRequest.value?.processItem.id,
    }
    await createProject(dtd)
    alertStore.show('Projekt erfolgreich erstellt', 'success')
    newProjectTitle.value = ''
    addingProject.value = false
  } catch (err: any) {
    console.error(err)
    alertStore.show(err.response?.data || 'Fehler beim Erstellen des Projekts', 'error')
  }
}

function showAddingTask() {
  addingTask.value = true
}

function showAddingProject() {
  addingProject.value = true
}

function cancelProject() {
  newProjectTitle.value = ''
  addingProject.value = false
}

function cancelTask() {
  newTaskTitle.value = ''
  addingTask.value = false
}

async function updateAssignee() {
  if (!editableRequest.value) return

  const id = assignee.value?.id ?? -1

  try {
    await assignProcessItemToUser(editableRequest.value.processItem.id, id)
  } catch (err: any) {
    const msg = err.response?.data?.message || err.response?.data || err.message || String(err)
    alertStore.show(`Fehler bei der Zuweisung): ${msg}`, 'error')
  }
}

// save changes
async function saveRequest() {
  console.log('save request')
  if (!editableRequest.value) return

  try {
    const dto: UpdateCustomerRequestDtd = {
      description: description.value,
      priority: editableRequest.value.priority,
      status: editableRequest.value.status,
      estimatedScope: estimatedScope.value,
      chargeable: editableRequest.value.chargeable,
      scopeUnit: editableRequest.value.scopeUnit,
    }

    await updateCustomerRequest(editableRequest.value.processItem.id, dto)
  } catch (err: any) {
    editableRequest.value = requestStore.selectedRequest

    const msg = err.response?.data?.message || err.response?.data || err.message || String(err)
    alertStore.show('Fehler beim Speichern: ' + msg, 'error')
  }
}

async function addComment() {
  if (!editableRequest.value || !commentText.value) return

  const commentCreateDtd: CommentCreateDtd = {
    text: commentText.value,
    //TODO add authorId from logged in user
    authorId: 1,
  }

  try {
    await addCommentToProcessItem(editableRequest.value.processItem.id, commentCreateDtd)
    alertStore.show('Kommentar erfolgreich erstellt', 'success')
    commentText.value = ''
  } catch (err: any) {
    console.error(err)
    alertStore.show(err.response?.data || 'Unbekannter Fehler', 'error')
  }
}
</script>

<template>
  <div v-if="editableRequest" class="flex h-screen gap-6 p-6">
    <!-- Left Area -->
    <ScrollArea class="flex-1 overflow-auto">
      <div class="p-6 space-y-4">
        <div>
          <Badge variant="secondary">{{ CategoryLabel[editableRequest.category] }}</Badge>
          <h2 class="text-xl font-bold">
            {{ editableRequest.processItem.id }} - {{ editableRequest.processItem.title }}
          </h2>
          <div class="flex gap-6 mt-4 text-sm">
            <div>
              <span class="font-semibold">Kunde: </span><br />
              {{ editableRequest.customer.id }} - {{ editableRequest.customer.firstName }}
            </div>
            <div>
              <span class="font-semibold">Eingegangen am: </span><br />
              {{ new Date(editableRequest.processItem.creationDate!).toLocaleDateString('de-DE') }}
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

          <AccordionItem value="attachment">
            <AccordionTrigger>Anhänge</AccordionTrigger>
            <AccordionContent>
              <AttachmentList
                :attachments="editableRequest.processItem.attachments"
                :processItemId="editableRequest.processItem.id"
              />
            </AccordionContent>
          </AccordionItem>

          <AccordionItem value="linked-tasks">
            <AccordionTrigger>Verknüpfte Aufgaben</AccordionTrigger>
            <AccordionContent class="flex flex-col gap-2">
              <!-- Tasks -->
              <div v-for="task in editableRequest.tasks" :key="task.processItem.id">
                <RouterLink :to="`/tasks/${task.processItem.id}`" class="block">
                  <div
                    class="flex items-center justify-between border p-2 rounded cursor-pointer hover:bg-accent/20"
                  >
                    <div class="flex items-center gap-2">
                      <span>{{ task.processItem.id }}</span>
                      <span class="font-semibold">{{ task.processItem.title }}</span>
                    </div>
                    <Badge variant="secondary">{{ task.status }}</Badge>
                  </div>
                </RouterLink>
              </div>

              <!-- Add Task Input -->
              <div v-if="addingTask" class="flex gap-2 items-center">
                <Input
                  v-model="newTaskTitle"
                  placeholder="Titel der neuen Aufgabe"
                  class="flex-1"
                />
                <Button @click="addTaskToRequest">Erstellen</Button>
                <Button variant="ghost" @click="cancelTask">Abbrechen</Button>
              </div>
              <!-- Show Add Button -->
              <div class="flex justify-end">
                <Button @click="showAddingTask">+</Button>
              </div>
            </AccordionContent>
          </AccordionItem>

          <AccordionItem value="linked-projects">
            <AccordionTrigger>Verknüpfte Projekte</AccordionTrigger>
            <AccordionContent class="flex flex-col gap-2">
              <!-- Projects -->
              <div v-for="project in editableRequest.projects" :key="project.processItem.id">
                <RouterLink :to="`/projects/${project.processItem.id}`" class="block">
                  <div
                    class="flex items-center justify-between border p-2 rounded cursor-pointer hover:bg-accent/20"
                  >
                    <div class="flex items-center gap-2">
                      <span>{{ project.processItem.id }}</span>
                      <span class="font-semibold">{{ project.processItem.title }}</span>
                    </div>

                    <Badge variant="secondary">{{ ProjectStatusLabel[project.status] }}</Badge>
                  </div>
                </RouterLink>
              </div>

              <div v-if="addingProject" class="flex gap-2 items-center">
                <Input
                  v-model="newProjectTitle"
                  placeholder="Titel des neuen Projekts"
                  class="flex-1"
                />
                <Button @click="addProjectToRequest">Erstellen</Button>
                <Button variant="secondary" @click="cancelProject">Abbrechen</Button>
              </div>

              <!-- Show Add Button -->
              <div class="flex justify-end">
                <Button @click="showAddingProject">+</Button>
              </div>
            </AccordionContent>
          </AccordionItem>

          <CommentsAccordion
            v-model="commentText"
            :comments="editableRequest.processItem.comments"
            @submit="addComment"
          />
        </Accordion>
      </div>
    </ScrollArea>

    <!-- right sidebar -->
    <div class="w-[200px] space-y-4 p-4 border-l-2 border-accent-200 h-screen">
      <div>
        <label class="text-sm font-semibold">Priorität</label>
        <Select v-model="editableRequest.priority" @update:modelValue="saveRequest">
          <SelectTrigger>
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
        <Select v-model="editableRequest.status" @update:modelValue="saveRequest">
          <SelectTrigger>
            <SelectValue placeholder="Offen" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem
              v-for="[value, requestStatusLabel] in Object.entries(RequestStatusLabel)"
              :key="value"
              :value="value"
            >
              {{ requestStatusLabel }}
            </SelectItem>
          </SelectContent>
        </Select>
      </div>

      <UserSelect v-model="assignee" @update:modelValue="updateAssignee" />

      <div>
        <label class="text-sm font-semibold">Geschätzte Zeit</label>

        <div class="flex space-x-2">
          <Input type="number" v-model="estimatedScope" placeholder="Schätzung in Minuten" />
          <Select v-model="editableRequest.scopeUnit" @update:modelValue="saveRequest">
            <SelectTrigger>
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
        <label class="text-sm font-semibold">Zu berechnen</label>
        <Select v-model="editableRequest.chargeable" @update:modelValue="saveRequest">
          <SelectTrigger>
            <SelectValue placeholder="Zeiteinheit" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem
              v-for="[value, chargeableLabel] in Object.entries(ChargeableLabel)"
              :key="value"
              :value="value"
            >
              {{ chargeableLabel }}
            </SelectItem>
          </SelectContent>
        </Select>
      </div>
    </div>
  </div>
</template>

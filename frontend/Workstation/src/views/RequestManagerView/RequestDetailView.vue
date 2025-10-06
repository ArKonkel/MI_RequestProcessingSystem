<script setup lang="ts">
import { ref, watch } from 'vue'

import { useRequestStore } from '@/stores/requestStore.ts'
import { useAlertStore } from '@/stores/useAlertStore.ts'
import { addCommentToProcessItem } from '@/services/commentService.ts'
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

const requestStore = useRequestStore()
const alertStore = useAlertStore()

const editableRequest = ref<RequestDtd | null>(null)
const commentText = ref('')
const description = ref('')
const estimatedScope = ref(0)

const addingTask = ref(false)
const newTaskTitle = ref('')

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
      console.log('ignore next update')
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

function showAddingTask() {
  addingTask.value = true
}

function cancelTask() {
  newTaskTitle.value = ''
  addingTask.value = false
}

// save changes
async function saveRequest() {
  console.log('save request')
  if (!editableRequest.value) return

  try {
    const dto = {
      description: description.value,
      priority: editableRequest.value.priority,
      status: editableRequest.value.status,
      assigneeId: editableRequest.value.processItem.assignee.id,
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

          <AccordionItem value="linked">
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

      <UserSelect v-model="editableRequest.processItem.assignee" @update:modelValue="saveRequest" />

      <div>
        <label class="text-sm font-semibold">Geschätzte Zeit</label>
        <Input type="number" v-model="estimatedScope" placeholder="Schätzung in Minuten" />
      </div>

      <div>
        <label class="text-sm font-semibold">Einheit</label>
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

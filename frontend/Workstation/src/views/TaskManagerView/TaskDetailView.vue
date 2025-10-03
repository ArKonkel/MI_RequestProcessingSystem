<script setup lang="ts">
import { ref, watch } from 'vue'

import { useTaskStore } from '@/stores/taskStore.ts'
import { useAlertStore } from '@/stores/useAlertStore.ts'
// import { updateTask } from '@/services/taskService.ts'

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

import { PriorityLabel } from '@/documentTypes/types/Priority.ts'
import { TaskStatusLabel } from '@/documentTypes/types/TaskStatus.ts'
import type { TaskDtd } from '@/documentTypes/dtds/TaskDtd.ts'
import { useDebounceFn } from '@vueuse/core'
import CommentsAccordion from "@/components/CommentsAccordion.vue";
import type {CommentCreateDtd} from "@/documentTypes/dtds/CommentCreateDtd.ts";
import {addCommentToProcessItem} from "@/services/commentService.ts";
import {TimeUnitLabel} from "@/documentTypes/types/TimeUnit.ts";

const taskStore = useTaskStore()
const alertStore = useAlertStore()

const editableTask = ref<TaskDtd | null>(null)
const commentText = ref('')
const description = ref('')
const acceptanceCriteria = ref('')
const estimatedTime = ref(0)
const workingTimeInMinutes = ref(0)

const ignoreNextUpdate = ref(false)

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
    } else {
      editableTask.value = null
      description.value = ''
      acceptanceCriteria.value = ''
      estimatedTime.value = 0
      workingTimeInMinutes.value = 0
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
    workingTimeInMinutes.value],
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
    const dto = {
      description: description.value,
      priority: editableTask.value.priority,
      status: editableTask.value.status,
      assigneeId: editableTask.value.processItem.assigneeId,
      estimatedTime: estimatedTime.value,
      acceptanceCriteria: acceptanceCriteria.value,
      workingTimeInMinutes: workingTimeInMinutes.value,
    }
    // await updateTask(editableTask.value.processItem.id, dto)
    //TODO
  } catch (err) {
    alertStore.show('Fehler beim Speichern', 'error')
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
          </div>
          <div class="flex gap-6 mt-4 text-sm">
            <div><span class="font-semibold">Anfrage</span><br />{{ editableTask.requestId }}</div>
            <div><span class="font-semibold">Projekt</span><br />{{ editableTask.projectId }}</div>
            <div>
              <span class="font-semibold">Geplant bis</span><br />{{
                new Date(editableTask.dueDate!).toLocaleDateString('de-DE')
              }}
            </div>
          </div>
        </div>

        <Accordion type="multiple" class="w-full" collapsible :defaultValue="['desc', 'acceptance', 'comments']">
          <AccordionItem value="desc">
            <AccordionTrigger>Beschreibung</AccordionTrigger>
            <AccordionContent>
              <Textarea
                v-model="description"
                class="mt-2 min-h-[200px] resize-none"
              />
            </AccordionContent>
          </AccordionItem>

          <AccordionItem value="acceptance">
            <AccordionTrigger>Akzeptanzkriterien</AccordionTrigger>
            <AccordionContent>
              <Textarea
                v-model="acceptanceCriteria"
                class="mt-2 min-h-[130px] resize-none"
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
    </ScrollArea>

    <!-- right sidebar -->
    <div class="w-[200px] space-y-4 p-4 border-l-2 border-accent-200 h-screen">
      <div>
        <label class="text-sm font-semibold">Priorität</label>
        <Select v-model="editableTask.priority" @update:modelValue="saveTask">
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
        <Select v-model="editableTask.status" @update:modelValue="saveTask">
          <SelectTrigger>
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

      <div>
        <label class="text-sm font-semibold">Zugewiesene Person</label>
        <Input v-model="editableTask.processItem.assigneeId" placeholder="Keine Person zugewiesen" />
      </div>

      <div>
        <label class="text-sm font-semibold">Geschätzte Zeit</label>
        <Input type="number" v-model="estimatedTime" placeholder="Schätzung in Minuten" />
      </div>

      <div class="border-b border-gray-300 pb-2 mb-2">
        <label class="text-sm font-semibold">Einheit</label>
        <Select v-model="editableTask.estimationUnit" @update:modelValue="saveTask">
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
        <label class="text-sm font-semibold">Aufgewandte Zeit (min)</label>
        <Input type="number" v-model="workingTimeInMinutes" placeholder="Zeit eintragen"
               @update:modelValue="saveTask" />
      </div>
    </div>
  </div>
</template>

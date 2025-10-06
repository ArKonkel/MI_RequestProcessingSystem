<script setup lang="ts">
import {ref, watch} from 'vue'
import {useProjectStore} from '@/stores/projectStore.ts'
import {useAlertStore} from '@/stores/useAlertStore.ts'
import {addCommentToProcessItem} from '@/services/commentService.ts'

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

import {ProjectStatusLabel} from '@/documentTypes/types/ProjectStatus.ts'
import type {ProjectDtd} from '@/documentTypes/dtds/ProjectDtd.ts'
import type {CommentCreateDtd} from '@/documentTypes/dtds/CommentCreateDtd.ts'

import {useDebounceFn} from '@vueuse/core'
import UserSelect from '@/components/UserSelect.vue'
import CommentsAccordion from '@/components/CommentsAccordion.vue'
import {Button} from '@/components/ui/button'
import type {TaskCreateDtd} from '@/documentTypes/dtds/TaskCreateDtd.ts'
import {createTask} from '@/services/taskService.ts'
import {ProjectDependencyTypeLabel} from "@/documentTypes/types/ProjectDependencyType.ts";

const projectStore = useProjectStore()
const alertStore = useAlertStore()

const editableProject = ref<ProjectDtd | null>(null)
const commentText = ref('')
const description = ref('')
const addingTask = ref(false)
const newTaskTitle = ref('')
const ignoreNextUpdate = ref(false)

// Watch: wenn ausgewähltes Projekt im Store wechselt
watch(
  () => projectStore.selectedProjects,
  (newProj) => {
    if (newProj) {
      editableProject.value = {...newProj}
      ignoreNextUpdate.value = true
      description.value = newProj.processItem.description
    } else {
      editableProject.value = null
      description.value = ''
    }
  },
  {immediate: true, deep: true},
)

// Debounced save
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

async function saveProject() {
  if (!editableProject.value) return
  try {
    const dto = {
      description: description.value,
      status: editableProject.value.status,
      assigneeId: editableProject.value.processItem.assignee.id,
      startDate: editableProject.value.startDate,
      endDate: editableProject.value.endDate,
    }

    console.log("project should be updated TODO")
    //await updateProject(editableProject.value.processItem.id, dto)
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
            <div>
              <span class="font-semibold">Request: </span><br/>
              {{ editableProject.requestId }} - {{ editableProject.requestTitle }}
            </div>
            <div>
              <span class="font-semibold">Start: </span><br/>
              {{ new Date(editableProject.startDate).toLocaleDateString('de-DE') }}
            </div>
            <div>
              <span class="font-semibold">Ende: </span><br/>
              {{ new Date(editableProject.endDate).toLocaleDateString('de-DE') }}
            </div>
          </div>
        </div>

        <Accordion type="multiple" class="w-full" collapsible
                   :defaultValue="['desc', 'tasks', 'comments']">
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
            <AccordionContent>
              <div v-for="dep in editableProject.projectDependencies" :key="dep.targetProjectId">
                <span class="font-semibold"> {{ ProjectDependencyTypeLabel[dep.type] }}: </span>
                {{ dep.targetProjectId }} - {{ dep.targetProjectTitle }}
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

    <!-- Sidebar -->
    <div class="w-[200px] space-y-4 p-4 border-l-2 border-accent-200 h-screen">
      <div>
        <label class="text-sm font-semibold">Status</label>
        <Select v-model="editableProject.status" @update:modelValue="saveProject">
          <SelectTrigger>
            <SelectValue placeholder="Status auswählen"/>
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

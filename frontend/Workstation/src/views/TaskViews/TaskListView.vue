<script setup lang="ts">
import {computed, onMounted, ref, watch} from 'vue'
import {useTaskStore} from '@/stores/taskStore.ts'
import {useRoute, useRouter} from 'vue-router'
import {ScrollArea} from '@/components/ui/scroll-area'
import {Card, CardContent, CardFooter, CardHeader, CardTitle} from '@/components/ui/card'
import {Badge} from '@/components/ui/badge'
import type {TaskDtd} from '@/documentTypes/dtds/TaskDtd.ts'
import {getPriorityColor, PriorityLabel} from '@/documentTypes/types/Priority.ts'
import {TaskStatus, TaskStatusLabel} from '@/documentTypes/types/TaskStatus.ts'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'

import {BookCheck} from "lucide-vue-next"

const taskStore = useTaskStore()
const router = useRouter()
const route = useRoute()

const selectedStatus = ref<TaskStatus | null>(null)

onMounted(async () => {
  await taskStore.fetchTasks()
})

// Watch für Routing-Param: Highlight selektierter Task
watch(
  () => route.params.taskId,
  (taskId) => {
    if (taskId) {
      taskStore.setSelectedTask(Number(taskId))
    }
  },
  {immediate: true},
)

// Gefilterte Tasks nach Status
const filteredTasks = computed(() => {
  const allTasks = taskStore.taskData?.tasks ?? []
  if (!selectedStatus.value) return allTasks
  return allTasks.filter((task) => task.status === selectedStatus.value)
})

function formatDate(date: string | null) {
  if (!date) return 'Kein Fälligkeitsdatum'
  return new Date(date).toLocaleDateString('de-DE', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
  })
}

function selectTask(task: TaskDtd) {
  taskStore.setSelectedTask(task.processItem.id)
  router.push({name: 'taskDetailView', params: {taskId: task.processItem.id}})
}
</script>

<template>
  <div class="flex-1 mb-4 w-60 justify-end">
    <Select v-model="selectedStatus">
      <SelectTrigger>
        <SelectValue placeholder="Alle Status"/>
      </SelectTrigger>
      <SelectContent>
        <SelectItem :value="null">Alle Status</SelectItem>
        <SelectItem
          v-for="[status, label] in Object.entries(TaskStatusLabel)"
          :key="status"
          :value="status"
        >
          {{ label }}
        </SelectItem>
      </SelectContent>
    </Select>
  </div>

  <ScrollArea class="h-screen rounded-md border overflow-y-auto p-4">
    <div class="flex flex-col gap-3">
      <Card
        v-for="task in filteredTasks"
        :key="task.processItem.id"
        @click="selectTask(task)"
        :class="[
          'hover:bg-accent/30 transition-colors cursor-pointer',
          taskStore.selectedTaskId === task.processItem.id
            ? 'bg-accent border-accent-foreground'
            : '',
        ]"
      >
        <CardHeader>
          <div class="flex items-center justify-between">
            <CardTitle>{{ task.processItem.id }} - {{ task.processItem.title }}</CardTitle>
            <Badge :variant="getPriorityColor(task.priority)">
              {{ PriorityLabel[task.priority] }}
            </Badge>
          </div>
        </CardHeader>

        <CardContent class="space-y-2">
          <div class="line-clamp-2 text-xs text-muted-foreground">
            {{ task.processItem.description.substring(0, 200) }}
          </div>
        </CardContent>

        <CardFooter class="flex flex-col items-start gap-2">
          <p class="text-sm text-muted-foreground">Fällig: {{ formatDate(task.dueDate) }}</p>

          <div class="w-full flex justify-between" >
            <div class="flex flex-wrap gap-2">
              <Badge
                v-for="expertise in task.expertise"
                :key="expertise.id"
                variant="outline"
                class="text-xs"
              >
                {{ expertise.name }}
              </Badge>
            </div>
            <div v-if="task.isAlreadyPlanned" class=" justify-end">
              <BookCheck class="stroke-1"/>
            </div>
          </div>
        </CardFooter>
      </Card>
    </div>
  </ScrollArea>
</template>

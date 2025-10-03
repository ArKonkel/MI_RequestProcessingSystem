<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useTaskStore } from '@/stores/taskStore.ts'
import { ScrollArea } from '@/components/ui/scroll-area'
import { Card, CardContent, CardFooter, CardHeader, CardTitle } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import type { TaskDtd } from '@/documentTypes/dtds/TaskDtd.ts'
import { getPriorityColor, PriorityLabel } from '@/documentTypes/types/Priority.ts'

const taskStore = useTaskStore()
const selectedTaskId = ref<number>() // für Highlight

onMounted(async () => {
  await taskStore.fetchTasks()
})

const tasks = computed(() => taskStore.taskData?.tasks ?? [])

function formatDate(date: string | null) {
  if (!date) return 'Kein Fälligkeitsdatum'
  return new Date(date).toLocaleDateString('de-DE', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
  })
}

function selectTask(task: TaskDtd) {
  taskStore.setSelectedTask(task)
}
</script>

<template>
  <ScrollArea class="h-screen rounded-md border overflow-y-auto p-4">
    <div class="flex flex-col gap-3">
      <Card
        v-for="task in tasks"
        :key="task.processItem.id"
        @click="selectTask(task)"
        :class="[
          'hover:bg-accent/30 transition-colors cursor-pointer',
          selectedTaskId === task.processItem.id ? 'bg-accent border-accent-foreground' : '',
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

          <div class="flex flex-wrap gap-2">
            <Badge
              v-for="competence in task.competences"
              :key="competence.id"
              variant="outline"
              class="text-xs"
            >
              {{ competence.name }}
            </Badge>
          </div>
        </CardFooter>
      </Card>
    </div>
  </ScrollArea>
</template>

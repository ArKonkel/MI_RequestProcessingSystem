import { defineStore } from 'pinia'
import type { TaskDtd } from '@/documentTypes/dtds/TaskDtd.ts'
import {computed, reactive, ref} from 'vue'
import axios from 'axios'
import { getAllTasks } from '@/services/taskService.ts'

export const useTaskStore = defineStore('taskStore', () => {
  const taskData = reactive({
    tasks: [] as TaskDtd[]
  })

  const selectedTaskId = ref<number | null>(null)

  const selectedTask = computed(
    () =>
      taskData.tasks.find((task) => task.processItem.id === selectedTaskId.value) ?? null,
  )

  async function fetchTasks() {
    try {
      taskData.tasks = await getAllTasks()

      if (!selectedTaskId.value && taskData.tasks.length > 0) {
        selectedTaskId.value = taskData.tasks[0].processItem.id
      }
    } catch (error) {
      console.error('Fehler beim Laden der Tasks:', error)
    }
  }

  //TODO add live update

  async function setSelectedTask(id: number) {
    selectedTaskId.value = id
  }

  return {
    taskData,
    selectedTaskId,
    selectedTask,
    fetchTasks,
    setSelectedTask,
  }
})

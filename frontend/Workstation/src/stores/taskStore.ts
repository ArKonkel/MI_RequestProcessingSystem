import { defineStore } from 'pinia'
import type { TaskDtd } from '@/documentTypes/dtds/TaskDtd.ts'
import {computed, reactive, ref} from 'vue'
import axios from 'axios'
import { getAllTasks } from '@/services/taskService.ts'
import {Client} from "@stomp/stompjs";

export const useTaskStore = defineStore('taskStore', () => {
  const wsurl = `/api/stompbroker`
  const DEST = '/topic/task'

  let stompClient: Client | null = null

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

      await startLiveUpdate()
    } catch (error) {
      console.error('Fehler beim Laden der Tasks:', error)
    }
  }

  async function startLiveUpdate() {
    if (!stompClient) {
      stompClient = new Client({ brokerURL: wsurl })
      stompClient.onWebSocketError = (event) => {
        throw new Error('WebSocket Error: ' + event)
      }
      stompClient.onStompError = (frameElement) => {
        throw new Error('Stompclient with Message: ' + frameElement)
      }
      stompClient.onConnect = () => {
        console.log('Stomp client connected')
        if (stompClient == null) {
          throw new Error('Stomp client connection failed')
        }

        stompClient.subscribe(DEST, (message) => {
          console.log('Received message: ' + message.body)
          fetchTasks() // Reload bei Updates
        })

        stompClient.onDisconnect = () => {
          console.log('Disconnected')
        }
      }

      stompClient.activate()
    }
  }

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

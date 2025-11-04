import { defineStore } from 'pinia'
import type { TaskDtd } from '@/documentTypes/dtds/TaskDtd.ts'
import { computed, reactive, ref } from 'vue'
import { getAllTasks, getTask } from '@/services/taskService.ts'
import { Client } from '@stomp/stompjs'
import type { ChangeNotificationEvent } from '@/documentTypes/dtds/ChangeNotificationEvent.ts'
import { ChangeType } from '@/documentTypes/types/ChangeType.ts'

export const useTaskStore = defineStore('taskStore', () => {
  const wsurl = `/api/stompbroker`
  const DEST = '/topic/task'

  let stompClient: Client | null = null

  const taskData = reactive({
    tasks: [] as TaskDtd[],
  })

  const selectedTaskId = ref<number | null>(null)

  let selectedTask = computed(
    () => taskData.tasks.find((task) => task.processItem.id === selectedTaskId.value) ?? null,
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

  async function updateTask(id: number) {
    try {
      const updatedTask = await getTask(id)

      taskData.tasks = taskData.tasks.map((task) =>
        task.processItem.id === id ? updatedTask : task,
      )

      selectedTask = computed(
        () => taskData.tasks.find((task) => task.processItem.id === selectedTaskId.value) ?? null,
      )
    } catch (error) {
      console.error(`Fehler beim Laden des Tasks mit id ${id}`, error)
    }
  }

  async function addNewTask(id: number) {
    try {
      const newTask = await getTask(id)

      const exists = taskData.tasks.some((task) => task.processItem.id === newTask.processItem.id)

      if (!exists) {
        taskData.tasks.push(newTask)
      }
    } catch (error) {
      console.error(`Fehler beim Laden des Tasks mit id ${id}`, error)
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
        console.log('Task Stomp client connected')
        if (stompClient == null) {
          throw new Error('Stomp client connection failed')
        }

        stompClient.subscribe(DEST, (message) => {
          const payload: ChangeNotificationEvent = JSON.parse(message.body)

          if (payload.changeType == ChangeType.UPDATED) {
            updateTask(payload.processItemId)
          } else if (payload.changeType == ChangeType.CREATED) {
            addNewTask(payload.processItemId)
          } else {
            fetchTasks()
          }
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

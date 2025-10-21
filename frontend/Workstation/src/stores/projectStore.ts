import { defineStore } from 'pinia'
import { reactive, ref, computed } from 'vue'
import { Client } from '@stomp/stompjs'
import type { ProjectDtd } from '@/documentTypes/dtds/ProjectDtd.ts'
import { getAllProjects, getProject } from '@/services/projectService.ts'
import { getTask } from '@/services/taskService.ts'
import type { ChangeNotificationEvent } from '@/documentTypes/dtds/ChangeNotificationEvent.ts'
import { ChangeType } from '@/documentTypes/types/ChangeType.ts'

export const useProjectStore = defineStore('projectStore', () => {
  const wsurl = `/api/stompbroker`
  const DEST = '/topic/project'

  let stompClient: Client | null = null

  const projectsData = reactive({
    projects: [] as ProjectDtd[],
  })

  const selectedProjectsId = ref<number | null>(null)

  const selectedProjects = computed(
    () =>
      projectsData.projects.find(
        (project) => project.processItem.id === selectedProjectsId.value,
      ) ?? null,
  )

  async function fetchProjects() {
    try {
      projectsData.projects = await getAllProjects()

      if (!selectedProjectsId.value && projectsData.projects.length > 0) {
        selectedProjectsId.value = projectsData.projects[0].processItem.id
      }

      await startLiveUpdate()
    } catch (error) {
      console.error('Fehler beim Laden der projects:', error)
    }
  }

  async function updateProject(id: number) {
    try {
      const updatedProject = await getProject(id)

      projectsData.projects = projectsData.projects.map((project) =>
        project.processItem.id === id ? updatedProject : project,
      )
    } catch (error) {
      console.error(`Fehler beim Laden des Projekts mit id ${id}`, error)
    }
  }

  async function addNewProject(id: number) {
    try {
      const newProject = await getProject(id)

      const exists = projectsData.projects.some(
        (project) => project.processItem.id === newProject.processItem.id,
      )

      if (!exists) {
        projectsData.projects.push(newProject)
      }
    } catch (error) {
      console.error(`Fehler beim Laden des Projekts mit id ${id}`, error)
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
        console.log('Project Stomp client connected')
        if (stompClient == null) {
          throw new Error('Stomp client connection failed')
        }

        stompClient.subscribe(DEST, (message) => {
          const payload: ChangeNotificationEvent = JSON.parse(message.body)

          if (payload.changeType == ChangeType.UPDATED) {
            updateProject(payload.processItemId)
          } else if (payload.changeType == ChangeType.CREATED) {
            addNewProject(payload.processItemId)
          } else {
            fetchProjects()
          }
        })

        stompClient.onDisconnect = () => {
          console.log('Disconnected')
        }
      }

      stompClient.activate()
    }
  }

  function setSelectedProjects(id: number) {
    selectedProjectsId.value = id
  }

  return {
    projectsData,
    selectedProjectsId,
    selectedProjects,
    fetchProjects,
    setSelectedProjects,
  }
})

import { defineStore } from 'pinia'
import { reactive, ref, computed } from 'vue'
import { Client } from '@stomp/stompjs'
import type {ProjectDtd} from "@/documentTypes/dtds/ProjectDtd.ts";
import {getAllProjects} from "@/services/projectService.ts";

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
      projectsData.projects.find((project) => project.processItem.id === selectedProjectsId.value) ?? null,
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
          fetchProjects() // Reload bei Updates
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

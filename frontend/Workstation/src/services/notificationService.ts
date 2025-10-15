import {Client} from "@stomp/stompjs";
import type {UserNotificationEvent} from "@/documentTypes/dtds/UserNotificationEvent.ts";
import {useAlertStore} from "@/stores/useAlertStore.ts";
import {useUserStore} from "@/stores/userStore.ts";
import {TargetType} from "@/documentTypes/types/TargetType.ts";

const wsurl = `/api/stompbroker`
const DEST_ASSIGNED = '/topic/processItem-assigned'
const DEST_COMMENT_MENTIONING = '/topic/in-comment-mentioned'

let stompClient: Client | null = null

export async function startListeningToNotifications() {
  const alertStore = useAlertStore()
  const userStore = useUserStore()

  if (!stompClient) {
    stompClient = new Client({brokerURL: wsurl})
    stompClient.onWebSocketError = (event) => {
      throw new Error('WebSocket Error: ' + event)
    }
    stompClient.onStompError = (frameElement) => {
      throw new Error('Stompclient with Message: ' + frameElement)
    }
    stompClient.onConnect = () => {
      console.log('Notification Stomp client connected')
      if (stompClient == null) {
        throw new Error('Stomp client connection failed')
      }

      stompClient.subscribe(DEST_ASSIGNED, (message) => {
        const payload: UserNotificationEvent = JSON.parse(message.body)

        if (!userStore.user) {
          alertStore.show("Nicht eingeloggt", 'error')
          return
        }

        const link = determineLink(payload)
        if (payload.userIdsToNotify.includes(userStore.user?.id)) {
          alertStore.show(`Zuweisung erhalten: ${payload.targetType} - ${payload.processItemId}: ${payload.processItemTitle}`, 'info', 8000, link)
        }
      })

      stompClient.subscribe(DEST_COMMENT_MENTIONING, (message) => {
        const payload: UserNotificationEvent = JSON.parse(message.body)

        if (!userStore.user) {
          alertStore.show("Nicht eingeloggt", 'error')
          return
        }

        const link = determineLink(payload)
        if (payload.userIdsToNotify.includes(userStore.user?.id)) {
          alertStore.show(`In Kommentar erwähnt:  ${payload.targetType} - ${payload.processItemId}: ${payload.processItemTitle}`, 'info', 8000, link)
        }
      })

      stompClient.onDisconnect = () => {
        console.log('Disconnected')
      }
    }

    stompClient.activate()
  }
}


function determineLink(userNotification: UserNotificationEvent) {
  let link = '/'

  if (userNotification.targetType == TargetType.TASK) {
    link = `/tasks/${userNotification.processItemId}`
  } else if (userNotification.targetType == TargetType.PROJECT) {
    link = `/projects/${userNotification.processItemId}`
  } else if (userNotification.targetType == TargetType.CUSTOMER_REQUEST) {
    link = `/requests/${userNotification.processItemId}`
  }

  return link
}

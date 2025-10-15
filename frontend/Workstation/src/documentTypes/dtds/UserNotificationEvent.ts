import type {UserNotificationType} from "@/documentTypes/types/UserNotificationType.ts";

export interface UserNotificationEvent {
  type: UserNotificationType
  processItemId: number
  processItemTitle: string
  userIdsToNotify: number[]
  text: string
  timeStamp: string
}

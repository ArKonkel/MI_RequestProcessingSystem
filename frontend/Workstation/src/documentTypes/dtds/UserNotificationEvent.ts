import type {UserNotificationType} from "@/documentTypes/types/UserNotificationType.ts";
import type {TargetType} from "@/documentTypes/types/TargetType.ts";

export interface UserNotificationEvent {
  type: UserNotificationType
  processItemId: number
  processItemTitle: string
  userIdsToNotify: number[]
  text: string
  timeStamp: string
  targetType: TargetType
}

import type {TargetType} from "@/documentTypes/types/TargetType.ts";
import type {ChangeType} from "@/documentTypes/types/ChangeType.ts";

export interface ChangeNotificationEvent {
  processItemId: number,
  changeType: ChangeType,
  targetType: TargetType
}

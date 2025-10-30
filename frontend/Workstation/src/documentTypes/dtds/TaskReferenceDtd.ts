import type {TaskStatus} from "@/documentTypes/types/TaskStatus.ts";

export interface TaskReferenceDtd {
  id: number
  title: string
  status: TaskStatus
}

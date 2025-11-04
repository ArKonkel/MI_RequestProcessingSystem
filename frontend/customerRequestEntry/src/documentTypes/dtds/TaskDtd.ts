import type { ProcessItemDtd } from '@/documentTypes/dtds/ProcessItemDtd.ts'
import type { Priority } from '@/documentTypes/types/Priority.ts'
import type { TaskStatus } from '@/documentTypes/types/TaskStatus.ts'

export interface TaskDtd {
  processItem: ProcessItemDtd
  priority: Priority
  status: TaskStatus
}

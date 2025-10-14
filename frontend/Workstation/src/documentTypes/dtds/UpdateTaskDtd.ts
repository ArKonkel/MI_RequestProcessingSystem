import type { Priority } from '@/documentTypes/types/Priority.ts'
import type { TaskStatus } from '@/documentTypes/types/TaskStatus.ts'
import type { TimeUnit } from '@/documentTypes/types/TimeUnit.ts'

export interface UpdateTaskDtd {
  title?: string
  description?: string
  estimatedTime?: number
  estimationUnit?: TimeUnit
  dueDate?: string
  status?: TaskStatus
  priority?: Priority
  acceptanceCriteria?: string
  expertiseIds?: number[]
}

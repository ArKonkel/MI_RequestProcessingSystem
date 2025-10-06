import type { Priority } from '@/documentTypes/types/Priority.ts'

export interface TaskCreateDtd {
  title: string
  description?: string
  dueDate?: string
  priority?: Priority
  requestId?: number
}

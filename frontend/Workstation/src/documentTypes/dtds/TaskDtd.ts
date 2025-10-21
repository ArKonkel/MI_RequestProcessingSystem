import type { ProcessItemDtd } from '@/documentTypes/dtds/ProcessItemDtd.ts'
import type { ExpertiseDtd } from '@/documentTypes/dtds/ExpertiseDtd.ts'
import type { TaskStatus } from '@/documentTypes/types/TaskStatus.ts'
import type { Priority } from '@/documentTypes/types/Priority.ts'
import type { TimeUnit } from '@/documentTypes/types/TimeUnit.ts'

export interface TaskDtd {
  processItem: ProcessItemDtd
  estimatedTime: number
  estimationUnit: TimeUnit
  workingTimeInMinutes: number
  dueDate: string | null
  priority: Priority
  acceptanceCriteria: string
  status: TaskStatus
  calendarEntryId: number | null
  expertise: ExpertiseDtd[]
  requestId: number | null
  requestTitle: string | null
  projectId: number | null
  projectTitle: string | null
  isAlreadyPlanned: boolean
}

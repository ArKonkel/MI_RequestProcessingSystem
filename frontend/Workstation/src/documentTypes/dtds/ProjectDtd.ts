import type { ProcessItemDtd } from '@/documentTypes/dtds/ProcessItemDtd.ts'
import type { ProjectStatus } from '@/documentTypes/types/ProjectStatus.ts'
import type { ProjectDependencyDtd } from '@/documentTypes/dtds/ProjectDependencyDtd.ts'
import type { TaskDtd } from '@/documentTypes/dtds/TaskDtd.ts'

export interface ProjectDtd {
  processItem: ProcessItemDtd
  status: ProjectStatus
  startDate: string
  endDate: string
  requestId: number
  requestTitle: string
  incomingDependencies: ProjectDependencyDtd[]
  outgoingDependencies: ProjectDependencyDtd[]
  tasks: TaskDtd[]
}

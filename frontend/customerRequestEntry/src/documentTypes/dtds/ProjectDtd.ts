import type { ProcessItemDtd } from '@/documentTypes/dtds/ProcessItemDtd.ts'
import type { ProjectStatus } from '@/documentTypes/types/ProjectStatus.ts'

export interface ProjectDtd {
  processItem: ProcessItemDtd
  status: ProjectStatus
}

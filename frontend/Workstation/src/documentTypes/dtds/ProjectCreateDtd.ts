import type { ProjectStatus } from '@/documentTypes/types/ProjectStatus.ts'

export interface ProjectCreateDtd {
  title: string
  description?: string
  status?: ProjectStatus
  startDate?: string
  endDate?: string
  requestId?: number
}

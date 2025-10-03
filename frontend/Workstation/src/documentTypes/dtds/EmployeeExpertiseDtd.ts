import type { ExpertiseDtd } from './ExpertiseDtd.ts'
import type { ExpertiseLevel } from '@/documentTypes/types/ExpertiseLevel.ts'

export interface EmployeeExpertiseDtd {
  id: number
  employeeId: number
  expertise: ExpertiseDtd
  level: ExpertiseLevel
}

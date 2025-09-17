import type { CompetenceDtd } from './CompetenceDtd'
import type {ExpertiseLevel} from "@/documentTypes/types/ExpertiseLevel.ts";

export interface EmployeeExpertiseDtd {
  id: number
  employeeId: number
  expertise: CompetenceDtd
  level: ExpertiseLevel
}

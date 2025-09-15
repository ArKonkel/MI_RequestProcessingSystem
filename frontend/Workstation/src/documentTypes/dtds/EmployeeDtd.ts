import type { CompetenceDtd } from './CompetenceDtd'
import type {EmployeeExpertiseDtd} from "@/documentTypes/dtds/EmployeeExpertiseDtd.ts";

export interface EmployeeDtd {
  id: number
  firstName: string
  lastName: string
  email: string
  phoneNumber: string | null
  hireDate: string | null
  workingHoursPerDay: number
  employeeExpertise: EmployeeExpertiseDtd[]
  competences: CompetenceDtd[]
  departmentId: number | null
  userId: number | null
  calendarId: number | null
}

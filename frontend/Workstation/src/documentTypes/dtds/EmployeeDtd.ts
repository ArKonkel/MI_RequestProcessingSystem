import type { ExpertiseDtd } from './ExpertiseDtd.ts'
import type {EmployeeExpertiseDtd} from "@/documentTypes/dtds/EmployeeExpertiseDtd.ts";

export interface EmployeeDtd {
  id: number
  firstName: string
  lastName: string
  email: string
  workingHoursPerDay: number
  employeeExpertise: EmployeeExpertiseDtd[]
  departmentId: number | null
  userId: number | null
  calendarId: number | null
}

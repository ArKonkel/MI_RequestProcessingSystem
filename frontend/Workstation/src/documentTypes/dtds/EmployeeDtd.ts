import type { EmployeeExpertiseDtd } from '@/documentTypes/dtds/EmployeeExpertiseDtd.ts'

export interface EmployeeDtd {
  id: number
  firstName: string
  lastName: string
  email: string
  workingHoursPerDay: number
  employeeExpertise: EmployeeExpertiseDtd[]
  departmentId?: number
  userId?: number
  calendarId?: number
}

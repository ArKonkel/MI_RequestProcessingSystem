import axios from 'axios'
import type { CalendarDtd } from '@/documentTypes/dtds/CalendarDtd.ts'

export async function getEmployeeCalendar(
  employeeId: number,
  from: string,
  to: string,
): Promise<CalendarDtd> {
  const response = await axios.get<CalendarDtd>(`/api/calendars/${employeeId}/${from}/${to}`)
  return response.data
}

export async function initCalendarOfEmployee(employeeId: number, year: number): Promise<void> {
  await axios.post(`/api/calendars/${employeeId}/${year}`)
}

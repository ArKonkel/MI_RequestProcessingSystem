import type {MatchingEmployeeForTaskDtd} from "@/documentTypes/dtds/MatchingEmployeeForTaskDtd.ts";
import axios from "axios";
import type {CalendarDtd} from "@/documentTypes/dtds/CalendarDtd.ts";

export async function getEmployeeCalendar(employeeId: number, from: string, to: string
): Promise<CalendarDtd> {
  const response = await axios.get<CalendarDtd>(`/api/calendar/${employeeId}/${from}/${to}`)
  return response.data
}

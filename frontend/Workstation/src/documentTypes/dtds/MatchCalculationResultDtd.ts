import type {EmployeeDtd} from "@/documentTypes/dtds/EmployeeDtd.ts";
import type {
  CalculatedCapacityCalendarEntryDtd
} from "@/documentTypes/dtds/CalculatedCapacityCalendarEntryDtd .ts";
import type {CalendarDtd} from "@/documentTypes/dtds/CalendarDtd.ts";

export interface MatchCalculationResultDtd {
  employee: EmployeeDtd
  competencePoints: number
  canCompleteTaskEarliest: boolean
  calculatedCalendarCapacities: CalculatedCapacityCalendarEntryDtd[]
  calendar?: CalendarDtd
}

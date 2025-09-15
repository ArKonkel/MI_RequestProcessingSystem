import type {EmployeeDtd} from "@/documentTypes/dtds/EmployeeDtd.ts";
import type {
  CalculatedCapacityCalendarEntryDtd
} from "@/documentTypes/dtds/CalculatedCapacityCalendarEntryDtd .ts";

export interface MatchCalculationResultDtd {
  employee: EmployeeDtd
  competencePoints: number
  canCompleteTaskEarliest: boolean
  calculatedCalendarCapacities: CalculatedCapacityCalendarEntryDtd[]
}

import type {TaskDtd} from "@/documentTypes/dtds/TaskDtd.ts";
import type {MatchCalculationResultDtd} from "@/documentTypes/dtds/MatchCalculationResultDtd.ts";

export interface MatchingEmployeeForTaskDtd {
  task: TaskDtd,
  matchCalculationResult: MatchCalculationResultDtd[]
}

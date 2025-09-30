import type {TaskDtd} from "@/documentTypes/dtds/TaskDtd.ts";
import type {CalculatedCapacitiesOfMatchDto} from "@/documentTypes/dtds/CalculatedCapacitiesOfMatchDto.ts";

export interface MatchingEmployeeCapacitiesDtd {
  taskId: number,
  matchCalculationResult: CalculatedCapacitiesOfMatchDto[]
}

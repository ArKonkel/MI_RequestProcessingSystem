import axios from "axios";
import type {MatchingEmployeeForTaskDtd} from "@/documentTypes/dtds/MatchingEmployeeForTaskDtd.ts";
import type {MatchCalculationResultDtd} from "@/documentTypes/dtds/MatchCalculationResultDtd.ts";

export async function getMatchingEmployees(taskId: number): Promise<MatchingEmployeeForTaskDtd> {
  const response = await axios.get<MatchingEmployeeForTaskDtd>(`/api/capacity/${taskId}`)
  return response.data
}

export async function assignTaskToEmployee(taskId: number, selectedMatch: MatchCalculationResultDtd): Promise<void> {
  await axios.post(`/api/capacity/assign/${taskId}`, selectedMatch);
}

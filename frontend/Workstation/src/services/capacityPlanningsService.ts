import axios from "axios";
import type {MatchingEmployeeForTaskDtd} from "@/documentTypes/dtds/MatchingEmployeeForTaskDtd.ts";

export async function getMatchingEmployees(taskId: number): Promise<MatchingEmployeeForTaskDtd> {
  const response = await axios.get<MatchingEmployeeForTaskDtd>(`/api/capacity/${taskId}`)
  return response.data
}

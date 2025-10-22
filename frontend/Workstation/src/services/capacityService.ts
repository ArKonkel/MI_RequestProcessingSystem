import axios from 'axios'
import type {
  MatchingEmployeeCapacitiesDtd
} from '@/documentTypes/dtds/MatchingEmployeeCapacitiesDtd.ts'
import type {
  CalculatedCapacitiesOfMatchDto
} from '@/documentTypes/dtds/CalculatedCapacitiesOfMatchDto.ts'

export async function getMatchingEmployees(taskId: number): Promise<MatchingEmployeeCapacitiesDtd> {
  const response = await axios.get<MatchingEmployeeCapacitiesDtd>(`/api/capacity/${taskId}`)
  return response.data
}

export async function calculateFreeCapacity(taskId: number, employeeId: number): Promise<MatchingEmployeeCapacitiesDtd> {
  const response = await axios.get<MatchingEmployeeCapacitiesDtd>(`/api/capacity/${taskId}/${employeeId}`)
  return response.data
}

export async function assignTaskToEmployee(
  taskId: number,
  selectedMatch: CalculatedCapacitiesOfMatchDto,
): Promise<void> {
  await axios.post(`/api/capacity/assign/${taskId}`, selectedMatch)
}

export async function deletePlannedCapacity(taskId: number): Promise<void> {
  await axios.delete(`/api/capacity/${taskId}`)
}

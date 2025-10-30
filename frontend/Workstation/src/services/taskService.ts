import type { TaskDtd } from '@/documentTypes/dtds/TaskDtd.ts'
import axios from 'axios'
import type { UpdateCustomerRequestDtd } from '@/documentTypes/dtds/UpdateCustomerRequestDtd.ts'
import type { RequestDtd } from '@/documentTypes/dtds/RequestDtd.ts'
import type { UpdateTaskDtd } from '@/documentTypes/dtds/UpdateTaskDtd.ts'
import type { TaskCreateDtd } from '@/documentTypes/dtds/TaskCreateDtd.ts'
import type { ProjectCreateDtd } from '@/documentTypes/dtds/ProjectCreateDtd.ts'
import type { WorkingTimeUnit } from '@/documentTypes/types/WorkingTimeUnit.ts'
import type { ExpertiseLevel } from '@/documentTypes/types/ExpertiseLevel.ts'

export async function getTask(taskId: number): Promise<TaskDtd> {
  const response = await axios.get<TaskDtd>(`/api/tasks/${taskId}`)
  return response.data
}

export async function getAllTasks(): Promise<TaskDtd[]> {
  const response = await axios.get<TaskDtd[]>(`/api/tasks`)
  return response.data
}

export async function updateTask(id: number, dto: UpdateTaskDtd): Promise<TaskDtd> {
  const response = await axios.patch(`/api/tasks/${id}`, dto)
  return response.data
}

export async function createTask(dto: TaskCreateDtd): Promise<TaskDtd> {
  const response = await axios.post(`/api/tasks`, dto)
  return response.data
}

export async function addWorkingTime(
  taskId: number,
  workingTime: number,
  unit: WorkingTimeUnit,
): Promise<void> {
  const response = await axios.post(`/api/tasks/${taskId}/workingTime`, null, {
    params: {
      workingTime,
      unit,
    },
  })

  return response.data
}

export async function addBlockingTask(taskId: number, blockedByTaskId: number): Promise<void> {
  await axios.post(`/api/tasks/${taskId}/${blockedByTaskId}`)
}

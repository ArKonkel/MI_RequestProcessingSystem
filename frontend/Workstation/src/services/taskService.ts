import type { TaskDtd } from '@/documentTypes/dtds/TaskDtd.ts'
import axios from 'axios'
import type { UpdateCustomerRequestDtd } from '@/documentTypes/dtds/UpdateCustomerRequestDtd.ts'
import type { RequestDtd } from '@/documentTypes/dtds/RequestDtd.ts'
import type { UpdateTaskDtd } from '@/documentTypes/dtds/UpdateTaskDtd.ts'
import type { TaskCreateDtd } from '@/documentTypes/dtds/TaskCreateDtd.ts'
import type {ProjectCreateDtd} from "@/documentTypes/dtds/ProjectCreateDtd.ts";

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

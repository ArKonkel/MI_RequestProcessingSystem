import type {TaskDtd} from "@/documentTypes/dtds/TaskDtd.ts";
import axios from "axios";


export async function getTask(taskId: number): Promise<TaskDtd> {
  const response = await axios.get<TaskDtd>(`/api/tasks/${taskId}`)
  return response.data
}

export async function getAllTasks(): Promise<TaskDtd[]> {
  const response = await axios.get<TaskDtd[]>(`/api/tasks`)
  return response.data
}

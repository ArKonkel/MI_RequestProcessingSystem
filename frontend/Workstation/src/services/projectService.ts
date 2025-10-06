import type {ProjectDtd} from "@/documentTypes/dtds/ProjectDtd.ts";
import axios from "axios";
import type {ProjectUpdateDtd} from "@/documentTypes/dtds/ProjectUpdateDtd.ts";
import type {ProjectCreateDtd} from "@/documentTypes/dtds/ProjectCreateDtd.ts";

export async function getAllProjects(): Promise<ProjectDtd[]> {
  const response = await axios.get('/api/projects')
  return response.data;
}

export async function updateProject(id: number, dto: ProjectUpdateDtd): Promise<ProjectDtd> {
  const response = await axios.patch(`/api/projects/${id}`, dto)
  return response.data
}

export async function createProject(dto: ProjectCreateDtd): Promise<ProjectDtd> {
  const response = await axios.post(`/api/projects`, dto)
  return response.data
}

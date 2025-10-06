import type {ProjectDtd} from "@/documentTypes/dtds/ProjectDtd.ts";
import axios from "axios";

export async function getAllProjects(): Promise<ProjectDtd[]> {
  const response = await axios.get('/api/projects')
  return response.data;
}

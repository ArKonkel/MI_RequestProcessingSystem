import type {ProjectDependencyType} from "@/documentTypes/types/ProjectDependencyType.ts";

export interface ProjectDependencyDtd {
  sourceProjectId: number
  targetProjectId: number
  targetProjectTitle: string
  type: ProjectDependencyType
}

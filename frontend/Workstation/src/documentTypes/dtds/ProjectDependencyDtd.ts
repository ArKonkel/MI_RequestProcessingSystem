import type {ProjectDependencyType} from "@/documentTypes/types/ProjectDependencyType.ts";

export interface ProjectDependencyDtd {
  sourceProjectId: number
  sourceProjectTitle: string
  targetProjectId: number
  targetProjectTitle: string
  type: ProjectDependencyType
}

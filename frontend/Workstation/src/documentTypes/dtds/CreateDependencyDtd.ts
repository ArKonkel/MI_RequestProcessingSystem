import type {ProjectDependencyType} from "@/documentTypes/types/ProjectDependencyType.ts";

export interface CreateDependencyDtd {
  sourceProjectId: number,
  targetProjectId: number,
  type: ProjectDependencyType
}

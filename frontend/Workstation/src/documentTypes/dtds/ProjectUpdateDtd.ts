import type {ProjectStatus} from "@/documentTypes/types/ProjectStatus.ts";

export interface ProjectUpdateDtd {
  title?: string,
  description?: string,
  status?: ProjectStatus,
  startDate?: string,
  endDate?: string,
}

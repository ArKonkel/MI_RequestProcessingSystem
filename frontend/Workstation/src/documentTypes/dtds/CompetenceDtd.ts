import type {CompetenceType} from "@/documentTypes/enums/CompetenceType.ts";

export interface CompetenceDtd {
  id: number;
  name: string;
  description: string;
  type: CompetenceType;
}

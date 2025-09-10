import type {StatusType} from "@/documentTypes/enums/StatusType.ts";

export interface StatusDtd {
  id: number;
  name: string;
  description: string;
  type: StatusType;
}

import type {StatusDtd} from "@/documentTypes/dtds/StatusDtd.ts";

export interface ProcessItemDtd {
  id: number;
  title: string;
  description: string;
  creationDate: string;
  assigneeId: number | null;
  status: StatusDtd;
}

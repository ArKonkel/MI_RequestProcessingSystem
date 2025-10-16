import type {CommentDtd} from "@/documentTypes/dtds/CommentDtd.ts";
import type {FileDtd} from "@/documentTypes/dtds/FileDtd.ts";

export interface ProcessItemDtd {
  id: number
  title: string
  description: string
  creationDate: string
  assigneeId: number
  attachments: FileDtd[]
  comments: CommentDtd[]
}

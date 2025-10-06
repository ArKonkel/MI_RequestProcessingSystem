import type { CommentDtd } from '@/documentTypes/dtds/CommentDtd.ts'
import type {UserDtd} from "@/documentTypes/dtds/UserDtd.ts";

export interface ProcessItemDtd {
  id: number
  title: string
  description: string
  creationDate: string
  assignee: UserDtd
  comments: CommentDtd[]
}

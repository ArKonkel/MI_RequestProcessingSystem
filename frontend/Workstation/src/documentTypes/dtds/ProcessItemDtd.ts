import type { CommentDtd } from '@/documentTypes/dtds/CommentDtd.ts'

export interface ProcessItemDtd {
  id: number
  title: string
  description: string
  creationDate: string
  assigneeId: number
  comments: CommentDtd[]
}

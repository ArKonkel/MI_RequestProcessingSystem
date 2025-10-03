import type { UserDtd } from '@/documentTypes/dtds/UserDtd.ts'

export interface CommentDtd {
  id: number
  text: string //instant
  timeStamp: string
  author: UserDtd
}

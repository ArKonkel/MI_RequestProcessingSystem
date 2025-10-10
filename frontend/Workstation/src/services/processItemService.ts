import type { CommentCreateDtd } from '@/documentTypes/dtds/CommentCreateDtd.ts'
import axios from 'axios'

export async function addCommentToProcessItem(processItemId: number, comment: CommentCreateDtd) {
  const response = await axios.post(`/api/processItems/${processItemId}/comments`, comment)
  return response.data
}

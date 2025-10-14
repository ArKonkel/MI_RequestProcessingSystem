import type {CommentCreateDtd} from '@/documentTypes/dtds/CommentCreateDtd.ts'
import axios from 'axios'

export async function addCommentToProcessItem(processItemId: number, comment: CommentCreateDtd): Promise<void> {
  const response = await axios.post(`/api/processItems/${processItemId}/comments`, comment)
  return response.data
}

export async function assignProcessItemToUser(processItemId: number, userId: number): Promise<void> {
  const response = await axios.post(`/api/processItems/${processItemId}/assign/${userId}`)
  return response.data
}

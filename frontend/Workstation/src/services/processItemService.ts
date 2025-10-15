import type { CommentCreateDtd } from '@/documentTypes/dtds/CommentCreateDtd.ts'
import axios from 'axios'
import type { FileDtd } from '@/documentTypes/dtds/FileDtd.ts'

export async function addCommentToProcessItem(
  processItemId: number,
  comment: CommentCreateDtd,
): Promise<void> {
  const response = await axios.post(`/api/processItems/${processItemId}/comments`, comment)
  return response.data
}

export async function assignProcessItemToUser(
  processItemId: number,
  userId: number,
): Promise<void> {
  const response = await axios.post(`/api/processItems/${processItemId}/assign/${userId}`)
  return response.data
}

export async function uploadAttachment(processItemId: number, file: File): Promise<FileDtd> {
  const formData = new FormData()
  formData.append('file', file)

  const response = await axios.post(`/api/processItems/${processItemId}/attachments`, formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  })

  return response.data
}

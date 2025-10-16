import type { CommentCreateDtd } from '@/documentTypes/dtds/CommentCreateDtd.ts'
import axios from 'axios'
import type { FileDtd } from '@/documentTypes/dtds/FileDtd.ts'

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

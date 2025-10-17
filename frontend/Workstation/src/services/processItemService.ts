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

export async function downloadAttachment(fileId: string): Promise<void> {
  const response = await axios.get(`/api/processItems/files/${fileId}`, {
    responseType: 'blob',
  })

  const blob = new Blob([response.data], { type: response.headers['content-type'] })

  const url = window.URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url

  const contentDisposition = response.headers['content-disposition']
  let fileName = 'download'
  if (contentDisposition) {
    const match = contentDisposition.match(/filename="(.+)"/) //finds filename in header
    if (match) fileName = match[1]
  }
  link.download = fileName

  //simulates click on link. Adds the link to the DOM and removes it afterward.
  document.body.appendChild(link)
  link.click()
  link.remove()
  window.URL.revokeObjectURL(url)
}


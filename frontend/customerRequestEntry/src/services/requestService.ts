import type { RequestCreateDtd } from '@/documentTypes/dtds/RequestCreateDtd.ts'
import type { RequestDtd } from '@/documentTypes/dtds/RequestDtd.ts'
import axios from 'axios'

export async function submitRequest(request: RequestCreateDtd, attachments:File[]): Promise<RequestDtd> {
  const formData = new FormData() //Formdata needed because of attachments

  formData.append('request', JSON.stringify(request))
  attachments.forEach(file => {
    formData.append('attachments', file)
  })

  const response = await axios.post(`/api/requests`, formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  })

  return response.data
}

export async function getRequestsFromCustomer(customerId: number): Promise<RequestDtd[]> {
  const response = await axios.get(`/api/requests/customer/${customerId}`)
  return response.data
}

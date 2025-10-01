import type { RequestCreateDtd } from '@/documentTypes/dtds/RequestCreateDtd.ts'
import type { RequestDtd } from '@/documentTypes/dtds/RequestDtd.ts'
import axios from 'axios'

export async function submitRequest(request: RequestCreateDtd): Promise<RequestDtd> {
  const response = await axios.post(`/api/requests`, request)
  return response.data
}

export async function getRequestsFromCustomer(customerId: number): Promise<RequestDtd[]> {
  const response = await axios.get(`/api/requests/customer/${customerId}`)
  return response.data
}

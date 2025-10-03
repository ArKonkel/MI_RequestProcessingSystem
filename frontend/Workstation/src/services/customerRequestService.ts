import axios from 'axios'
import type { RequestDtd } from '@/documentTypes/dtds/RequestDtd.ts'
import type { UpdateCustomerRequestDtd } from '@/documentTypes/dtds/UpdateCustomerRequestDtd.ts'

export async function getRequests(): Promise<RequestDtd[]> {
  const response = await axios.get(`/api/requests`)
  return response.data
}

export async function updateCustomerRequest(
  id: number,
  dto: UpdateCustomerRequestDtd,
): Promise<RequestDtd> {
  const response = await axios.patch(`/api/requests/${id}`, dto)
  return response.data
}

import type { UserDtd } from '@/documentTypes/dtds/UserDtd.ts'
import axios from 'axios'

export async function getAllUser(): Promise<UserDtd[]> {
  const response = await axios.get('/api/users')
  return response.data
}

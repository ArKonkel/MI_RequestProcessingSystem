import type {UserDtd} from '@/documentTypes/dtds/UserDtd.ts'
import axios from 'axios'

export async function getUserByName(name: string): Promise<UserDtd> {
  const response = await axios.get(`/api/users/${name}`)
  return response.data
}

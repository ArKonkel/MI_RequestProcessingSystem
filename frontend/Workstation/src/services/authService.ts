import type { LoginDtd } from '@/documentTypes/dtds/LoginDtd.ts'
import axios from 'axios'

export async function login(loginData: LoginDtd): Promise<string> {
  const result = await axios.post('/api/auth/login', loginData, {
    headers: { 'Content-Type': 'application/json' },
    withCredentials: true,
  })
  return result.data
}

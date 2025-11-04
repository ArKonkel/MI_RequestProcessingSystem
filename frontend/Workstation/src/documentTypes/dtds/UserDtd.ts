import type { RoleDtd } from '@/documentTypes/dtds/RoleDtd.ts'

export interface UserDtd {
  id: number
  name: string
  roles: RoleDtd[]
  employeeId?: number
}

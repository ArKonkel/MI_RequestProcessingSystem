import type { RoleDtd } from '@/documentTypes/dtds/RoleDtd.ts'
import type { CustomerDtd } from '@/documentTypes/dtds/CustomerDtd.ts'

export interface UserDtd {
  id: number
  name: string
  roles: RoleDtd[]
  customer: CustomerDtd
}

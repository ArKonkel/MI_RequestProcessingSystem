import type { ProcessItemDtd } from '@/documentTypes/dtds/ProcessItemDtd.ts'
import type { Priority } from '@/documentTypes/types/Priority.ts'
import type { RequestStatus } from '@/documentTypes/types/RequestStatus.ts'
import type { Category } from '@/documentTypes/types/Category.ts'

export interface RequestDtd {
  processItem: ProcessItemDtd
  priority: Priority
  estimatedScope: number
  status: RequestStatus
  chargeable: boolean
  category: Category
  customerId: number
}

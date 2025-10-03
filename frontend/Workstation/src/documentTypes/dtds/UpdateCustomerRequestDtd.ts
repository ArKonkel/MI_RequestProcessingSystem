import type { TimeUnit } from '@/documentTypes/types/TimeUnit.ts'
import type { Category } from '@/documentTypes/types/Category.ts'
import type { RequestStatus } from '@/documentTypes/types/RequestStatus.ts'
import type { Chargeable } from '@/documentTypes/types/Chargeable.ts'
import type { Priority } from '@/documentTypes/types/Priority.ts'

export interface UpdateCustomerRequestDtd {
  title?: string
  description?: string
  assigneeId?: number
  priority?: Priority
  estimatedScope?: number
  scopeUnit?: TimeUnit
  status?: RequestStatus
  chargeable?: Chargeable
  category?: Category
}

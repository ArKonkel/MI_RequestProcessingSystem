import type { TimeUnit } from '@/documentTypes/types/TimeUnit.ts'
import type { Category } from '@/documentTypes/types/Category.ts'
import type { RequestStatus } from '@/documentTypes/types/RequestStatus.ts'
import type { Chargeable } from '@/documentTypes/types/Chargeable.ts'
import type { Priority } from '@/documentTypes/types/Priority.ts'
import type { IsProjectClassification } from '@/documentTypes/types/IsProjectClassification.ts'

export interface UpdateCustomerRequestDtd {
  title?: string
  description?: string
  priority?: Priority
  estimatedScope?: number
  scopeUnit?: TimeUnit
  status?: RequestStatus
  chargeable?: Chargeable
  category?: Category
  classifiedAsProject?: IsProjectClassification
}

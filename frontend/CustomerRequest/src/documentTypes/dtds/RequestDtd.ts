import type { ProcessItemDtd } from '@/documentTypes/dtds/ProcessItemDtd.ts'
import type { Priority } from '@/documentTypes/types/Priority.ts'
import type { RequestStatus } from '@/documentTypes/types/RequestStatus.ts'
import type { Category } from '@/documentTypes/types/Category.ts'
import type {Chargeable} from "@/documentTypes/types/Chargeable.ts";
import type {CustomerDtd} from "@/documentTypes/dtds/CustomerDtd.ts";

export interface RequestDtd {
  processItem: ProcessItemDtd
  priority: Priority
  estimatedScope: number
  status: RequestStatus
  chargeable: Chargeable
  category: Category
  customer: CustomerDtd
}

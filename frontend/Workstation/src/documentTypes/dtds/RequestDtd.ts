import type { ProcessItemDtd } from '@/documentTypes/dtds/ProcessItemDtd.ts'
import type { Priority } from '@/documentTypes/types/Priority.ts'
import type { RequestStatus } from '@/documentTypes/types/RequestStatus.ts'
import type { Category } from '@/documentTypes/types/Category.ts'
import type { CustomerDtd } from '@/documentTypes/dtds/CustomerDtd.ts'
import type { Chargeable } from '@/documentTypes/types/Chargeable.ts'
import type { TimeUnit } from '@/documentTypes/types/TimeUnit.ts'
import type { TaskDtd } from '@/documentTypes/dtds/TaskDtd.ts'
import type { ProjectDtd } from '@/documentTypes/dtds/ProjectDtd.ts'
import type {IsProjectClassification} from "@/documentTypes/types/IsProjectClassification.ts";

export interface RequestDtd {
  contactFirstName: string,
  contactLastName: string,
  processItem: ProcessItemDtd
  priority: Priority
  estimatedScope: number
  scopeUnit: TimeUnit
  status: RequestStatus
  chargeable: Chargeable
  category: Category
  customer: CustomerDtd
  classifiedAsProject: IsProjectClassification
  programNumber: string
  contactPhoneNumber: string
  tasks: TaskDtd[]
  projects: ProjectDtd[]
}

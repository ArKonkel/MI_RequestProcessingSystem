import type {ProcessItemDtd} from "@/documentTypes/dtds/ProcessItemDtd.ts";
import type {Priority} from "@/documentTypes/enums/Priority.ts";
import type {RequestStatus} from "@/documentTypes/enums/RequestStatus.ts";
import type {Category} from "@/documentTypes/enums/Category.ts";

export interface RequestDtd {
  processItem: ProcessItemDtd,
  priority: Priority,
  estimatedScope: number,
  status: RequestStatus,
  chargeable: boolean,
  category: Category,
  customerId: number,
}

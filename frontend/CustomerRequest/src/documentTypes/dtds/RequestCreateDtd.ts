import type { Category } from '@/documentTypes/types/Category.ts'
import type { Priority } from '@/documentTypes/types/Priority.ts'
import type { ProcessItemCreateDtd } from '@/documentTypes/dtds/ProcessItemCreateDtd.ts'

export interface RequestCreateDtd {
  processItem: ProcessItemCreateDtd
  priority: Priority | null //must be null for formular init
  category: Category | null
  customerId: number | null
}

import type { Category } from '@/documentTypes/types/Category.ts'
import type { Priority } from '@/documentTypes/types/Priority.ts'
import type { ProcessItemCreateDtd } from '@/documentTypes/dtds/ProcessItemCreateDtd.ts'
import type { EmailAddressDtd } from '@/documentTypes/dtds/EmailAddressDtd.ts'

export interface RequestCreateDtd {
  contactFirstName: string
  contactLastName: string
  processItem: ProcessItemCreateDtd
  priority: Priority | null //must be null for formular init
  category: Category | null
  customerId: number | null
  toRecipients: EmailAddressDtd[]
  programNumber: string
  contactPhoneNumber: string
  module: string
}

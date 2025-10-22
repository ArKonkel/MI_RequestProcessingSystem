import type { CalendarEntryDtd } from '@/documentTypes/dtds/CalendarEntryDtd.ts'

export interface CalendarDtd {
  id: number
  entries: CalendarEntryDtd[]
  ownerId: number,
  ownerFirstName: string,
  ownerLastName: string,
}

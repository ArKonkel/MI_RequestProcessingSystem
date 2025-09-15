import type {CalendarEntryDtd} from "@/documentTypes/dtds/CalendarEntryDtd.ts";

export interface CalendarDtd {
  id: number;
  entries: CalendarEntryDtd[];
  employeeId: number;
}

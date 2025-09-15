export interface CalendarEntryDtd {
  id: number;
  title: string;
  description: string | null;
  date: string;
  duration: number; // in minutes
}

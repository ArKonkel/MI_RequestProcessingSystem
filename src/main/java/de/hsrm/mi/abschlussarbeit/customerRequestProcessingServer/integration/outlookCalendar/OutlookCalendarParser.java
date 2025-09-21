package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.integration.outlookCalendar;


import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.calendar.CalendarEntryDto;

public interface OutlookCalendarParser {

    CalendarEntryDto parseCalendarEntry(IncomingCalendarEntryDto incomingCalendarEntryDto);

}

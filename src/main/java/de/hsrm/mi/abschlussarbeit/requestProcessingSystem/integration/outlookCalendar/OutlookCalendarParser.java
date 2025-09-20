package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.integration.outlookCalendar;


import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.calendar.CalendarEntryDto;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.calendar.IncomingCalendarEntryDto;

public interface OutlookCalendarParser {

    CalendarEntryDto parseCalendarEntry(IncomingCalendarEntryDto incomingCalendarEntryDto);

}

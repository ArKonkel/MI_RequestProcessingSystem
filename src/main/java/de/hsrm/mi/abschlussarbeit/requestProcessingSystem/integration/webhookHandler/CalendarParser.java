package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.integration.webhookHandler;


import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.calendarModule.dto.CalendarEntryDto;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.calendarModule.dto.IncomingCalendarEntryDto;

public interface CalendarParser {

    CalendarEntryDto parseCalendarEntry(IncomingCalendarEntryDto incomingCalendarEntryDto);

}

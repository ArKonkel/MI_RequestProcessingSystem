package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.calendar;

import java.util.List;

public record CalendarDto (
        Long id,
        List<CalendarEntryDto> entries,
        Long employeeId
) {}

package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.calendarModule.dto;

import java.util.List;

public record CalendarDto (
        Long id,
        List<CalendarEntryDto> entries,
        Long employeeId
) {}

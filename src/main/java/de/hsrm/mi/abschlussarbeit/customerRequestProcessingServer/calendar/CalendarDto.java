package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.calendar;

import java.math.BigDecimal;
import java.util.List;

public record CalendarDto (
        Long id,
        List<CalendarEntryDto> entries,
        Long ownerId,
        String ownerFirstName,
        String ownerLastName,
        BigDecimal ownerWorkingHoursPerDay
) {}

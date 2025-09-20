package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.capacity;

import java.time.LocalDate;

public record CalculatedCapacityCalendarEntryDto(
        String title,
        LocalDate date,
        Long duration
) {}

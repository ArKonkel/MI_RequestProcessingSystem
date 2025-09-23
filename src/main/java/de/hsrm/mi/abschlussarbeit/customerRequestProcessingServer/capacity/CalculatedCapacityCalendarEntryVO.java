package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.capacity;

import java.time.LocalDate;

public record CalculatedCapacityCalendarEntryVO(
        String title,
        LocalDate date,
        Long durationInMinutes
) {}

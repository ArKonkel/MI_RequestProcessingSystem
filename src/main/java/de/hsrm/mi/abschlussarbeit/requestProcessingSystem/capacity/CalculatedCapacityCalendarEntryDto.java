package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.capacity;

import java.time.LocalDate;

public record CalculatedCapacityCalendarEntryDto(
        String title,
        LocalDate date,
        Long duration
) {}

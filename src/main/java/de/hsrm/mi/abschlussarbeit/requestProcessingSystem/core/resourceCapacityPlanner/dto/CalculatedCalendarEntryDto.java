package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.resourceCapacityPlanner.dto;

import java.time.LocalDate;

public record CalculatedCalendarEntryDto(
        String title,
        LocalDate date,
        Long duration
) {}

package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.resourceCapacityPlanner.dto;

import java.time.LocalDate;

public record CalculatedCapacityCalendarEntryDto(
        String title,
        LocalDate date,
        Long duration
) {}

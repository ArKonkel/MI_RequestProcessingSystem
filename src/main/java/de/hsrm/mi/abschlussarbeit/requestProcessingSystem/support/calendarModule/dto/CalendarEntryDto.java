package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.calendarModule.dto;


import java.time.LocalDate;

public record CalendarEntryDto (
        Long id,
        String title,
        String description,
        LocalDate date,
        Long duration
) {}

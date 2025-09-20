package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.calendar;


import java.time.LocalDate;

public record CalendarEntryDto (
        Long id,
        String title,
        String description,
        LocalDate date,
        Long duration
) {}

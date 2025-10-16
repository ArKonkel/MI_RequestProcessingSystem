package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.calendar;


import java.time.LocalDate;

public record CalendarEntryDto (
        Long id,
        String title,
        String description,
        LocalDate date,
        Long durationInMinutes,
        Long taskId
) {}

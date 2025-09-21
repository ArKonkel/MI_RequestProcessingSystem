package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.capacity;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CalculatedCapacityCalendarEntryDto {
    private String title;
    private LocalDate date;
    private Long duration;
}


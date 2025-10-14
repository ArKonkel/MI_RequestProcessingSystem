package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.project;

import jakarta.validation.constraints.Future;

import java.time.LocalDate;

public record ProjectUpdateDto(
        String title,
        String description,
        ProjectStatus status,
        LocalDate startDate,
        @Future
        LocalDate endDate
) {
}

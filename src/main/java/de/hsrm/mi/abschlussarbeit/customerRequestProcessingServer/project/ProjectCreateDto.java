package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.project;

import java.time.LocalDate;

public record ProjectCreateDto(
        String title,
        String description,
        ProjectStatus status,
        LocalDate startDate,
        LocalDate endDate,
        Long requestId
) {
}

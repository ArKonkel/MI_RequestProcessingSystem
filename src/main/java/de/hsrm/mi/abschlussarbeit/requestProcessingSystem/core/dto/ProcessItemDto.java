package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.dto;

import java.time.LocalDate;
import java.util.Set;

public record ProcessItemDto (
        Long id,
        String title,
        String description,
        LocalDate creationDate,
        Long assigneeId,
        Long statusId,
        Set<Long> commentIds
) {}

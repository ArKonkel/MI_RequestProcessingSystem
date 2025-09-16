package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.dto;

import java.time.LocalDate;

public record ProcessItemDto (
        Long id,
        String title,
        String description,
        LocalDate creationDate,
        Long assigneeId
        //Set<Long> commentIds
) {}

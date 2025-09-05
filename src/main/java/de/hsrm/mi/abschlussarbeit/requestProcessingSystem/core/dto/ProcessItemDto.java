package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.dto;

import java.util.Date;
import java.util.Set;

public record ProcessItemDto (
        long id,
        String title,
        String description,
        Date creationDate,
        Long assigneeId,
        Long statusId,
        Set<Long> commentIds
) {}

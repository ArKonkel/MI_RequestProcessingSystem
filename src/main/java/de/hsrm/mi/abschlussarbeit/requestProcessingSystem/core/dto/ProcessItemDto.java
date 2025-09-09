package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.dto;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.statusManager.dto.StatusDto;

import java.time.LocalDate;
import java.util.Set;

public record ProcessItemDto (
        Long id,
        String title,
        String description,
        LocalDate creationDate,
        Long assigneeId,
        StatusDto status,
        Set<Long> commentIds
) {}

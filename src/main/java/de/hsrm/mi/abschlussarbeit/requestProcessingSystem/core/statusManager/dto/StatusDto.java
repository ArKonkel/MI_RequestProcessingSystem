package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.statusManager.dto;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.enums.StatusType;

public record StatusDto(
        Long id,
        String name,
        String description,
        StatusType type
) {}
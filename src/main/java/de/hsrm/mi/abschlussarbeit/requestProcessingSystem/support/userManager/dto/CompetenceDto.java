package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.userManager.dto;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.enums.CompetenceType;

public record CompetenceDto(
        Long id,
        String name,
        String description,
        CompetenceType type
) {}
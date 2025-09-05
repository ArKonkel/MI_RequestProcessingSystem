package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.userManager.dto;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.enums.CompetenceType;

public record CompetenceDto(
        long id,
        String name,
        String description,
        CompetenceType type
) {}
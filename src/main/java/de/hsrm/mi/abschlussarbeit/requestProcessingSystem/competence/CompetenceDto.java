package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.competence;

public record CompetenceDto(
        Long id,
        String name,
        String description,
        CompetenceType type
) {}
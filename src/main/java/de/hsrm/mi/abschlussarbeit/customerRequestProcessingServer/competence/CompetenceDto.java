package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.competence;

public record CompetenceDto(
        Long id,
        String name,
        String description,
        CompetenceType type
) {}
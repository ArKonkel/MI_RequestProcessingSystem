package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.project;

public record ProjectDependencyDto(
        Long sourceProjectId,
        String sourceProjectTitle,
        Long targetProjectId,
        String targetProjectTitle,
        ProjectDependencyType type) {
}

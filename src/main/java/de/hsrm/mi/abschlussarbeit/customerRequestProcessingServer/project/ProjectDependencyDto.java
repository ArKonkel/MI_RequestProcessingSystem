package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.project;

public record ProjectDependencyDto(
        Long sourceProjectId,
        Long targetProjectId,
        String targetProjectTitle,
        ProjectDependencyType type) {
}

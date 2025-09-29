package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.project;

public record CreateDependencyDto(Long sourceProjectId, Long targetProjectId, ProjectDependencyType type) {
}

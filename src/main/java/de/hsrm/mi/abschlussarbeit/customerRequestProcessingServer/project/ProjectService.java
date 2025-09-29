package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.project;

public interface ProjectService {

    Project getProjectById(Long projectId);

    ProjectDto getProjectDtoById(Long projectId);

    boolean isProjectReadyForProcessing(Long projectId);

    void updateProjectStatus(Long projectId, ProjectStatus newStatus);

    ProjectDependency createProjectDependency(Long sourceProjectId, Long targetProjectId, ProjectDependencyType type);
}

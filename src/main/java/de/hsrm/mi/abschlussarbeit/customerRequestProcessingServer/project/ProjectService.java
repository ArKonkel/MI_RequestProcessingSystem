package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.project;

import java.util.List;

public interface ProjectService {

    Project getProjectById(Long projectId);

    ProjectDto getProjectDtoById(Long projectId);

    List<ProjectDto> getAllDtoProjects();

    boolean isProjectReadyForProcessing(Long projectId);

    ProjectDto updateProject(Long projectId, ProjectUpdateDto updateDto);

    void updateProjectStatus(Long projectId, ProjectStatus newStatus);

    ProjectDependency createProjectDependency(Long sourceProjectId, Long targetProjectId, ProjectDependencyType type);
}

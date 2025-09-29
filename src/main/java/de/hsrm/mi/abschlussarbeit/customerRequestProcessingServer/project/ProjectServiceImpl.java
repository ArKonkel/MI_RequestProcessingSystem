package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.project;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.globalExceptionHandler.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;

    private final ProjectDependencyRepository dependencyRepository;

    public Project getProjectById(Long projectId) {
        log.info("Getting project with id {}", projectId);

        return projectRepository.findById(projectId).orElseThrow(() -> new NotFoundException("Project with id " + projectId + " not found"));
    }

    @Override
    public ProjectDto getProjectDtoById(Long projectId) {
        //TODO implement me
        return null;
    }

    @Override
    public boolean isProjectReadyForProcessing(Long projectId) {
        Project project = getProjectById(projectId);

        return (project.getStatus().equals(ProjectStatus.READY) || project.getStatus().equals(ProjectStatus.IN_PROGRESS));
    }

    @Override
    public void updateProjectStatus(Long projectId, ProjectStatus newStatus) {
        log.info("Updating project {} to status {}", projectId, newStatus);

        Project project = getProjectById(projectId);

        boolean allowed = switch (newStatus) {
            case READY -> canMoveToReady(project);
            case IN_PROGRESS -> canMoveToInProgress(project);
            case FINISHED -> canMoveToFinished(project);
            default -> true;
        };

        if (!allowed)
            throw new BlockedByDependencyException("Project cannot be moved to status " + newStatus + " because of dependencies." +
                    " Project: " + project.getId() + " Status: " + project.getStatus() + " Dependencies: " + project.getIncomingDependencies());

        project.setStatus(newStatus);
        projectRepository.save(project);
    }

    @Override
    public ProjectDependency createProjectDependency(Long sourceProjectId, Long targetProjectId, ProjectDependencyType type) {
        log.info("Creating project dependency from {} to {} with type {}", sourceProjectId, targetProjectId, type);

        Project sourceProject = getProjectById(sourceProjectId);
        Project targetProject = getProjectById(targetProjectId);

        ProjectDependency dependency = new ProjectDependency();
        dependency.setSourceProject(sourceProject);
        dependency.setTargetProject(targetProject);
        dependency.setType(type);

        dependencyRepository.save(dependency);

        return dependency;
    }


    /**
     * Checks if the project can be moved to READY.
     * Checks if all Dependencies, that can block the status change to READY.
     */
    private boolean canMoveToReady(Project project) {
        if (project.getIncomingDependencies() == null || project.getIncomingDependencies().isEmpty()) {
            return true;
        }
        for (ProjectDependency dependency : project.getIncomingDependencies()) {
            Project source = dependency.getSourceProject();
            ProjectDependencyType type = dependency.getType();
            switch (type) {
                // Can only be set to READY when sourceProject is finished
                case FINISH_TO_START, USES_RESULT -> {
                    if (source.getStatus() != ProjectStatus.FINISHED) {
                        return false;
                    }
                }
                // Can only be set to Ready if sourceProject is READY, IN_PROGRESS or FINISHED
                case START_TO_START, PART_OF -> {
                    if (source.getStatus() != ProjectStatus.READY
                            && source.getStatus() != ProjectStatus.IN_PROGRESS
                            && source.getStatus() != ProjectStatus.FINISHED) {
                        return false;
                    }
                }
                // FINISH_TO_FINISH does not block Ready.
                case FINISH_TO_FINISH -> {
                    continue;
                }
            }
        }
        return true;
    }

    /**
     * Checks if the Project can be set to IN_PROGRESS.
     * The project itself must be ready, and the Dependencies should be able to set to Ready
     */
    private boolean canMoveToInProgress(Project project) {
        return project.getStatus() == ProjectStatus.READY && canMoveToReady(project);
    }

    /**
     * Checks if the Project can be set to FINISHED.
     * Can only be set to FINISHED if all Dependencies are FINISHED. (FINISH_TO_FINISH)
     */
    private boolean canMoveToFinished(Project project) {
        if (project.getIncomingDependencies() == null || project.getIncomingDependencies().isEmpty()) {
            return true;
        }
        for (ProjectDependency dependency : project.getIncomingDependencies()) {
            Project source = dependency.getSourceProject();
            ProjectDependencyType type = dependency.getType();

            if (type == ProjectDependencyType.FINISH_TO_FINISH) {
                if (source.getStatus() != ProjectStatus.FINISHED) {
                    return false;
                }
            }
        }
        return true;
    }
}
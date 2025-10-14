package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.project;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.customerRequest.CustomerRequest;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.customerRequest.CustomerRequestService;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.globalExceptionHandler.NotFoundException;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.notification.ChangeNotificationEvent;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.notification.ChangeType;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.notification.NotificationService;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.notification.TargetType;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;

    private final ProjectDependencyRepository dependencyRepository;

    private final CustomerRequestService customerRequestService;

    private final ProjectMapper projectMapper;

    private final NotificationService notificationService;

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
    @Transactional(readOnly = true)//Needed because fetching blob
    public List<ProjectDto> getAllDtoProjects() {
        log.info("Getting all dto projects");

        return projectRepository.findAllByOrderByCreationDateDescIdDesc().stream().map(projectMapper::toDto).toList();
    }

    @Override
    public boolean isProjectReadyForProcessing(Long projectId) {
        Project project = getProjectById(projectId);

        return (project.getStatus().equals(ProjectStatus.READY) || project.getStatus().equals(ProjectStatus.IN_PROGRESS));
    }

    @Override
    public ProjectDto updateProject(Long projectId, ProjectUpdateDto updateDto) {
        log.info("Updating project {} with {}", projectId, updateDto);

        Project project = getProjectById(projectId);

        if (updateDto.title() != null) {
            project.setTitle(updateDto.title());
        }
        if (updateDto.description() != null) {
            project.setDescription(updateDto.description());
        }
        if (updateDto.status() != null) {
            updateProjectStatus(projectId, updateDto.status());
        }

        if (updateDto.startDate() != null) {
            project.setStartDate(updateDto.startDate());
        }

        if (updateDto.endDate() != null) {
            project.setEndDate(updateDto.endDate());
        }

        Project savedProject = projectRepository.save(project);

        notificationService.sendChangeNotification(new ChangeNotificationEvent(savedProject.getId(), ChangeType.UPDATED, TargetType.PROJECT));

        return projectMapper.toDto(savedProject);
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
        //projectRepository.save(project);
    }

    @Override
    public ProjectDto createProject(ProjectCreateDto createDto) {
        Project projectToCreate = new Project();

        if (createDto.title() != null) {
            projectToCreate.setTitle(createDto.title());
        }
        if (createDto.description() != null) {
            projectToCreate.setDescription(createDto.description());
        }
        if (createDto.status() != null) {
            projectToCreate.setStatus(createDto.status());
        }
        if (createDto.startDate() != null) {
            projectToCreate.setStartDate(createDto.startDate());
        }
        if (createDto.endDate() != null) {
            projectToCreate.setEndDate(createDto.endDate());
        }

        if (createDto.requestId() != null) {
            CustomerRequest customerRequest = customerRequestService.getRequestById(createDto.requestId());
            projectToCreate.setRequest(customerRequest);
        }

        Project savedProject = projectRepository.save(projectToCreate);

        notificationService.sendChangeNotification(new ChangeNotificationEvent(savedProject.getId(), ChangeType.CREATED, TargetType.PROJECT));

        //Also send notification to Customer-Requests because it was created
        if (savedProject.getRequest() != null) {
            notificationService.sendChangeNotification(new ChangeNotificationEvent(savedProject.getRequest().getId(), ChangeType.UPDATED, TargetType.CUSTOMER_REQUEST));
        }

        return projectMapper.toDto(savedProject);
    }

    @Override
    public ProjectDependency createProjectDependency(Long sourceProjectId, Long targetProjectId, ProjectDependencyType type) {
        log.info("Creating project dependency from {} to {} with type {}", sourceProjectId, targetProjectId, type);

        Project sourceProject = getProjectById(sourceProjectId);
        Project targetProject = getProjectById(targetProjectId);

        if (sourceProject.getId().equals(targetProject.getId())) {
            throw new InvalidDependencyException("Cannot create dependency between project and itself");
        }

        ProjectDependency dependency = new ProjectDependency();
        dependency.setSourceProject(sourceProject);
        dependency.setTargetProject(targetProject);
        dependency.setType(type);

        dependencyRepository.save(dependency);

        notificationService.sendChangeNotification(new ChangeNotificationEvent(sourceProject.getId(), ChangeType.UPDATED, TargetType.PROJECT));

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
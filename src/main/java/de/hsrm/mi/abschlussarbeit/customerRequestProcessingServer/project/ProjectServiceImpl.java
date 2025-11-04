package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.project;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.customerRequest.CustomerRequest;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.customerRequest.CustomerRequestService;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.customerRequest.IsProjectClassification;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.globalExceptionHandler.NotAllowedException;
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

    /**
     * Retrieves a project by its unique identifier.
     *
     * @param projectId the unique identifier of the project to retrieve
     * @return the project associated with the given identifier
     * @throws NotFoundException if no project is found with the specified identifier
     */
    @Transactional
    public Project getProjectById(Long projectId) {
        log.info("Getting project with id {}", projectId);

        return projectRepository.findById(projectId).orElseThrow(() -> new NotFoundException("Project with id " + projectId + " not found"));
    }

    /**
     * Retrieves the DTO representation of a project by its unique identifier.
     * This method fetches the project from the database and maps it to a DTO.
     *
     * @param projectId the unique identifier of the project to retrieve
     * @return a {@link ProjectDto} object representing the project
     */
    @Override
    @Transactional(readOnly = true) //Needed because fetching blob
    public ProjectDto getProjectDtoById(Long projectId) {
        log.info("Getting project dto with id {}", projectId);

        return projectMapper.toDto(getProjectById(projectId));
    }

    /**
     * Retrieves all projects from the database, ordered by creation date and ID in descending order,
     * and maps them to a list of ProjectDto objects.
     *
     * @return a list of ProjectDto objects representing all projects
     */
    @Override
    @Transactional(readOnly = true)
    public List<ProjectDto> getAllDtoProjects() {
        log.info("Getting all dto projects");

        return projectRepository.findAllByOrderByCreationDateDescIdDesc().stream().map(projectMapper::toDto).toList();
    }

    /**
     * Determines if the specified project is ready for processing based on its status.
     *
     * @param projectId the unique identifier of the project to check
     * @return true if the project's status is READY or IN_PROGRESS, false otherwise
     */
    @Override
    @Transactional
    public boolean isProjectReadyForProcessing(Long projectId) {
        Project project = getProjectById(projectId);

        return (project.getStatus().equals(ProjectStatus.READY) || project.getStatus().equals(ProjectStatus.IN_PROGRESS));
    }

    /**
     * Updates the specified project with new details provided in the ProjectUpdateDto.
     * The method allows partial updates of the project.
     *
     * @param projectId the ID of the project to update
     * @param updateDto the data transfer object containing updated details for the project
     * @return a ProjectDto representing the updated project
     */
    @Override
    @Transactional
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

    /**
     * Updates the status of a project to a specified new status.
     * This method checks if the project is allowed to transition to the given status
     * based on its dependencies and current state. If the transition is not allowed,
     * a {@link BlockedByDependencyException} is thrown.
     *
     * @param projectId The ID of the project to be updated.
     * @param newStatus The new status to which the project should be updated.
     *                  Allowed values are READY, IN_PROGRESS, and FINISHED.
     */
    @Override
    @Transactional
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
            throw new BlockedByDependencyException("Project " + project.getId() + " cannot be moved to status " + newStatus + " because of dependencies.");

        project.setStatus(newStatus);
        //projectRepository.save(project);
    }

    /**
     * Creates a new project based on the provided data and persists it to the repository.
     * Sends notifications for the project creation and, if applicable, updates related customer requests.
     *
     * @param createDto the data transfer object containing information for creating the project,
     *                  such as title, description, status, startDate, endDate, and related customer request ID.
     * @return the data transfer object representing the created project.
     * @throws NotAllowedException if the associated customer request is not classified as a project.
     */
    @Override
    @Transactional
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
            if (customerRequest.getClassifiedAsProject() != null && !customerRequest.getClassifiedAsProject().equals(IsProjectClassification.YES)) {
                throw new NotAllowedException("CustomerRequest is not classified as Project.");
            }

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

    /**
     * Creates a dependency between two projects with a specified dependency type.
     *
     * @param sourceProjectId the ID of the source project from which the dependency originates
     * @param targetProjectId the ID of the target project to which the dependency points
     * @param type the type of dependency to be created
     * @return the created ProjectDependency object representing the relationship between the two projects
     * @throws InvalidDependencyException if the source project and the target project are the same
     */
    @Override
    @Transactional
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
     * Determines whether a project can be moved to the "Ready" state based on its dependencies and their statuses.
     *
     * @param project the project whose readiness is being evaluated
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
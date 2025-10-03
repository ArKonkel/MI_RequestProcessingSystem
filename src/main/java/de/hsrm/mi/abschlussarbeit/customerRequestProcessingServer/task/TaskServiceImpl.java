package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.task;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.customerRequest.CustomerRequest;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.customerRequest.CustomerRequestService;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.globalExceptionHandler.NotAllowedException;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.globalExceptionHandler.NotFoundException;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.globalExceptionHandler.SaveException;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.notification.ChangeNotificationEvent;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.notification.ChangeType;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.notification.NotificationService;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.notification.TargetType;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.project.ProjectService;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.user.User;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.user.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class TaskServiceImpl implements TaskService {
    private final NotificationService notificationService;

    private final TaskRepository taskRepository;

    private final TaskMapper taskMapper;

    private final UserService userService;

    private final ProjectService projectService;

    private final CustomerRequestService customerRequestService;

    @Override
    public Task getTaskById(Long id) {
        log.info("Getting task with id {}", id);

        return taskRepository.findById(id).orElseThrow(() -> new NotFoundException("Task with id " + id + " not found."));
    }

    @Override
    public TaskDto getTaskDtoById(Long id) {
        log.info("Getting task dto with id {}", id);
        Task task = getTaskById(id);

        return taskMapper.toDto(task);
    }

    @Override
    public List<TaskDto> getAllTasks() {
        log.info("Getting all tasks");
        return taskRepository.findAllByOrderByCreationDateDescIdDesc().stream().map(taskMapper::toDto).toList();
    }

    @Override
    public TaskDto createTask(TaskCreateDto createDto) {
        Task taskToCreate = new Task();

        if (createDto.title != null) {
            taskToCreate.setTitle(createDto.title);
        }

        if (createDto.description != null) {
            taskToCreate.setDescription(createDto.description);
        }

        if (createDto.dueDate != null) {
            taskToCreate.setDueDate(createDto.dueDate);
        }

        if (createDto.priority != null) {
            taskToCreate.setPriority(createDto.priority);
        }

        if (createDto.requestId != null) {
            CustomerRequest customerRequest = customerRequestService.getRequestById(createDto.requestId);
            taskToCreate.setRequest(customerRequest);
        }


        Task savedTask = taskRepository.save(taskToCreate);

        notificationService.sendChangeNotification(new ChangeNotificationEvent(savedTask.getId(), ChangeType.CREATED, TargetType.TASK));

        //Also send notification to Customer-Requests because it was created
        if(savedTask.getRequest() != null) {
            notificationService.sendChangeNotification(new ChangeNotificationEvent(savedTask.getRequest().getId(), ChangeType.UPDATED, TargetType.CUSTOMER_REQUEST));
        }

        return taskMapper.toDto(savedTask);
    }

    public TaskDto updateTask(Long taskId, UpdateTaskDto updateDto) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));

        if (updateDto.getEstimatedTime() != null) {
            task.setEstimatedTime(updateDto.getEstimatedTime());
        }

        if (updateDto.getEstimationUnit() != null) {
            task.setEstimationUnit(updateDto.getEstimationUnit());
        }

        if (updateDto.getWorkingTimeInMinutes() != null) {
            task.setWorkingTimeInMinutes(updateDto.getWorkingTimeInMinutes());
        }

        if (updateDto.getDueDate() != null) {
            task.setDueDate(updateDto.getDueDate());
        }

        if (updateDto.getAcceptanceCriteria() != null) {
            task.setAcceptanceCriteria(updateDto.getAcceptanceCriteria());
        }

        if (updateDto.getPriority() != null) {
            task.setPriority(updateDto.getPriority());
        }

        if (updateDto.getStatus() != null) {
            //Status can only be updated when project xor request is ready for processing.
            if (task.getProject() != null &&
                    !projectService.isProjectReadyForProcessing(task.getProject().getId())) {
                throw new NotAllowedException("Task status cannot be changed if project is not ready for processing");
            }

            if (task.getRequest() != null &&
                    !customerRequestService.isRequestReadyForProcessing(task.getRequest().getId())) {
                throw new NotAllowedException("Task status cannot be changed if customer request is not ready for processing");
            }

            task.setStatus(updateDto.getStatus());
        }

        if (updateDto.getTitle() != null) {
            task.setTitle(updateDto.getTitle());
        }
        if (updateDto.getDescription() != null) {
            task.setDescription(updateDto.getDescription());
        }
        if (updateDto.getAssigneeId() != null) {
            User assignee = userService.getUserById(updateDto.getAssigneeId());
            task.setAssignee(assignee);
        }

        Task savedTask = taskRepository.save(task);

        notificationService.sendChangeNotification(new ChangeNotificationEvent(savedTask.getId(), ChangeType.UPDATED, TargetType.TASK));

        return taskMapper.toDto(savedTask);
    }

    /**
     * Adds the given working time to the task with the given id.
     *
     * @param taskId      of task to add working time to. Must exist.
     * @param workingTime to add.
     * @param unit        of working time.
     */
    @Override
    public void addWorkingTime(Long taskId, BigDecimal workingTime, WorkingTimeUnit unit) {
        log.info("Adding {} {} to task {}", workingTime, unit, taskId);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("Task with id " + taskId + " not found"));

        Long currentWorkingTime = task.getWorkingTimeInMinutes() != null ? task.getWorkingTimeInMinutes() : 0L;
        Long workingTimeInMinutes = calculateWorkingTimeUnitIntoMinutes(workingTime, unit) + currentWorkingTime;

        task.setWorkingTimeInMinutes(workingTimeInMinutes);

        try {
            taskRepository.save(task);
        } catch (Exception e) {

            throw new SaveException("Error while saving task " + taskId + " with message" + e);
        }
    }

    /**
     * Calculates the working time in minutes for a given working time and unit.
     *
     * @param workingTime to calculate
     * @param unit        the working time is given
     * @return calculated working time in minutes
     */
    private Long calculateWorkingTimeUnitIntoMinutes(BigDecimal workingTime, WorkingTimeUnit unit) {
        if (workingTime == null || unit == null) {
            throw new IllegalArgumentException("Working time and unit must not be null");
        }

        return switch (unit) {
            case MINUTES -> workingTime.setScale(0, RoundingMode.HALF_UP).longValue();
            case HOURS -> workingTime.multiply(BigDecimal.valueOf(60))
                    .setScale(0, RoundingMode.HALF_UP)
                    .longValue();
            default -> throw new UnsupportedOperationException("Unsupported WorkingTimeUnit: " + unit);
        };
    }

}

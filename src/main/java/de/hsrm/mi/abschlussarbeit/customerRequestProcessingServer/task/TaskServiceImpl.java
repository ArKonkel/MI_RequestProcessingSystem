package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.task;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.customerRequest.CustomerRequest;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.globalExceptionHandler.NotFoundException;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.globalExceptionHandler.SaveException;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.processItem.UpdateProcessItemDto;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.project.Project;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.user.User;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.user.UserDto;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.user.UserService;
import jakarta.transaction.Transactional;
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

    private final TaskRepository taskRepository;

    private final TaskMapper taskMapper;

    private final UserService userService;

    @Override
    public TaskDto getTaskDtoById(Long id) {
        log.info("Getting task dto with id {}", id);

        return taskMapper.toDto(taskRepository.getReferenceById(id));
    }

    @Override
    public Task getTaskById(Long id) {
        log.info("Getting task with id {}", id);

        return taskRepository.getReferenceById(id);
    }

    @Override
    public List<TaskDto> getAllTasks() {
        log.info("Getting all tasks");
        return taskRepository.findAll().stream().map(taskMapper::toDto).toList();
    }

    @Transactional
    public TaskDto updateTask(Long taskId, UpdateTaskDto dto) {
        log.info("Updating task with id {}", taskId);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        //Could be done by mapper. However, it's not working.
        //ProcessItem
        if (dto.getProcessItem() != null) {
            UpdateProcessItemDto processItem = dto.getProcessItem();
            if (processItem.getTitle() != null) task.setTitle(processItem.getTitle());
            if (processItem.getDescription() != null) task.setDescription(processItem.getDescription());
            if (processItem.getAssigneeId() != null) {
                User assignee = new User();
                assignee.setId(processItem.getAssigneeId());
                task.setAssignee(assignee);
            }
        }

        //Task
        if (dto.getStatus() != null) task.setStatus(dto.getStatus());
        if (dto.getEstimatedTime() != null) task.setEstimatedTime(dto.getEstimatedTime());
        if (dto.getWorkingTime() != null) task.setWorkingTimeInMinutes(dto.getWorkingTime());
        if (dto.getDueDate() != null) task.setDueDate(dto.getDueDate());
        if (dto.getAcceptanceCriteria() != null) task.setAcceptanceCriteria(dto.getAcceptanceCriteria());
        if (dto.getPriority() != null) task.setPriority(dto.getPriority());

        if (dto.getRequestId() != null) {
            CustomerRequest request = new CustomerRequest();
            request.setId(dto.getRequestId());
            task.setRequest(request);
        }

        if (dto.getProjectId() != null) {
            Project project = new Project();
            project.setId(dto.getProjectId());
            task.setProject(project);
        }

        taskRepository.save(task);
        return taskMapper.toDto(task);
    }

    @Override
    @Transactional
    public void assignTaskToUserOfEmployee(Long taskId, Long employeeId) {
        log.info("Assigning task {} to user of employee {}", taskId, employeeId);

        UserDto userDto = userService.getUserOfEmployee(employeeId);

        UpdateProcessItemDto processItemDto = UpdateProcessItemDto.builder()
                .assigneeId(userDto.id())
                .build();

        UpdateTaskDto updateTaskDto = UpdateTaskDto.builder().
                processItem(processItemDto).
                build();

        updateTask(taskId, updateTaskDto);
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

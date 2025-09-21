package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.task;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.processItem.UpdateProcessItemDto;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.project.Project;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.customerRequest.Request;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.user.User;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    private final TaskMapper taskMapper;

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
        if(dto.getStatus() != null) task.setStatus(dto.getStatus());
        if (dto.getEstimatedTime() != null) task.setEstimatedTime(dto.getEstimatedTime());
        if (dto.getWorkingTime() != null) task.setWorkingTime(dto.getWorkingTime());
        if (dto.getDueDate() != null) task.setDueDate(dto.getDueDate());
        if (dto.getAcceptanceCriteria() != null) task.setAcceptanceCriteria(dto.getAcceptanceCriteria());
        if (dto.getPriority() != null) task.setPriority(dto.getPriority());

        if (dto.getBlockerId() != null) {
            Task blocker = new Task();
            blocker.setId(dto.getBlockerId());
            task.setBlocker(blocker);
        }

        if (dto.getBlockedId() != null) {
            Task blocked = new Task();
            blocked.setId(dto.getBlockedId());
            task.setBlocked(blocked);
        }

        if (dto.getReferenceTaskId() != null) {
            Task reference = new Task();
            reference.setId(dto.getReferenceTaskId());
            task.setReferenceTask(reference);
        }

        if (dto.getRequestId() != null) {
            Request request = new Request();
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
}

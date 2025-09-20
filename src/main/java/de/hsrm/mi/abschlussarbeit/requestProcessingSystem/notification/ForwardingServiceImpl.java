package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.notification;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.processItem.UpdateProcessItemDto;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.task.UpdateTaskDto;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.task.TaskManager;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.user.UserDto;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.user.UserManager;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class ForwardingServiceImpl implements ForwardingService {

    private final TaskManager taskManager;

    private final UserManager userManager;

    @Override
    public void assignTaskToUserOfEmployee(Long taskId, Long employeeId) {
        log.info("Assigning task {} to user of employee {}", taskId, employeeId);

        UserDto userDto = userManager.getUserOfEmployee(employeeId);

        UpdateProcessItemDto processItemDto = UpdateProcessItemDto.builder()
                .assigneeId(userDto.id())
                .build();

        UpdateTaskDto updateTaskDto = UpdateTaskDto.builder().
                processItem(processItemDto).
                build();

        taskManager.updateTask(taskId, updateTaskDto);
    }
}

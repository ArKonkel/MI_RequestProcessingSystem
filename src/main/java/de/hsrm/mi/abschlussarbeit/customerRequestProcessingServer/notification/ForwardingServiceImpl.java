package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.notification;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.processItem.UpdateProcessItemDto;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.task.UpdateTaskDto;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.task.TaskManager;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.user.UserDto;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.user.UserManager;
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

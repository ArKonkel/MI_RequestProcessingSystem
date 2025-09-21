package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.notification;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.processItem.UpdateProcessItemDto;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.task.TaskService;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.task.UpdateTaskDto;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.user.UserDto;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.user.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class ForwardingServiceImpl implements ForwardingService {

    private final TaskService taskService;

    private final UserService userService;

    @Override
    public void assignTaskToUserOfEmployee(Long taskId, Long employeeId) {
        log.info("Assigning task {} to user of employee {}", taskId, employeeId);

        UserDto userDto = userService.getUserOfEmployee(employeeId);

        UpdateProcessItemDto processItemDto = UpdateProcessItemDto.builder()
                .assigneeId(userDto.id())
                .build();

        UpdateTaskDto updateTaskDto = UpdateTaskDto.builder().
                processItem(processItemDto).
                build();

        taskService.updateTask(taskId, updateTaskDto);
    }
}

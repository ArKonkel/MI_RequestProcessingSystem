package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.interactionManager.service;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.dto.UpdateProcessItemDto;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.taskManager.dto.UpdateTaskDto;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.taskManager.service.TaskManager;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.userManager.dto.UserDto;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.userManager.service.UserManager;
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

package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.task;

import java.math.BigDecimal;
import java.util.List;

public interface TaskService {

    TaskDto getTaskDtoById(Long id);

    Task getTaskById(Long id);

    List<TaskDto> getAllTasks();

    TaskDto updateTask(Long taskId, UpdateTaskDto dto);

    void assignTaskToUserOfEmployee(Long taskId, Long employeeId); //TODO gehört zu processItem

    void addWorkingTime(Long taskId, BigDecimal workingTime, WorkingTimeUnit unit);

}

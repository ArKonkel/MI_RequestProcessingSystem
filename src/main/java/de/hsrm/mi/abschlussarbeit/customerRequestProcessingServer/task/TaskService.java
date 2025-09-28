package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.task;

import java.math.BigDecimal;
import java.util.List;

public interface TaskService {

    Task getTaskById(Long id);

    List<TaskDto> getAllTasks();

    TaskDto updateTask(Long taskId, UpdateTaskDto dto);

    void addWorkingTime(Long taskId, BigDecimal workingTime, WorkingTimeUnit unit);

}

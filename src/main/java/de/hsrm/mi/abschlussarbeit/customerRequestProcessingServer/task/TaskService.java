package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.task;

import java.math.BigDecimal;
import java.util.List;

public interface TaskService {

    Task getTaskById(Long id);

    TaskDto getTaskDtoById(Long id);

    List<TaskDto> getAllTasks();

    TaskDto createTask(TaskCreateDto createDto);

    TaskDto updateTask(Long taskId, UpdateTaskDto dto);

    void addWorkingTime(Long taskId, BigDecimal workingTime, WorkingTimeUnit unit);

    void setIsAlreadyPlanned(Long taskId, Boolean isAlreadyPlanned);
}

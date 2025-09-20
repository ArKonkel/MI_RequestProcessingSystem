package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.task;

import java.util.List;

public interface TaskService {

    TaskDto getTaskById(Long id);

    List<TaskDto> getAllTasks();

    TaskDto updateTask(Long taskId, UpdateTaskDto dto);
}

package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.taskManager.service;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.taskManager.dto.TaskDto;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.taskManager.dto.UpdateTaskDto;

import java.util.List;

public interface TaskService {

    TaskDto getTaskById(Long id);

    List<TaskDto> getAllTasks();

    TaskDto updateTask(Long taskId, UpdateTaskDto dto);
}

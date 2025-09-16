package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.taskManager.service;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.taskManager.dto.TaskDto;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.taskManager.dto.UpdateTaskDto;

public interface TaskManager {

    TaskDto getTaskById(Long id);

    TaskDto updateTask(Long taskId, UpdateTaskDto updateTask);
}

package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.taskManager.service;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.taskManager.dto.TaskDto;

import java.util.List;

public interface TaskService {

    TaskDto getTaskById(Long id);

    List<TaskDto> getAllTasks();
}

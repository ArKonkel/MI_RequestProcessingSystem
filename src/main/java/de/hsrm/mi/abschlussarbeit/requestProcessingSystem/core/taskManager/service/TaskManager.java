package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.taskManager.service;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.taskManager.dto.TaskDto;

public interface TaskManager {

    TaskDto getTaskById(Long id);
}

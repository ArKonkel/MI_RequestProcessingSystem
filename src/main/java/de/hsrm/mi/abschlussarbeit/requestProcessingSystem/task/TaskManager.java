package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.task;

public interface TaskManager {

    TaskDto getTaskById(Long id);

    TaskDto updateTask(Long taskId, UpdateTaskDto updateTask);
}

package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.task;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class TaskManagerImpl implements TaskManager {

    private final TaskService taskService;

    @Override
    public TaskDto getTaskById(Long id) {

        return taskService.getTaskById(id);
    }

    @Override
    public TaskDto updateTask(Long taskId, UpdateTaskDto updateTask) {

       return taskService.updateTask(taskId, updateTask);
    }
}

package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.taskManager.service;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.taskManager.dto.TaskDto;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.taskManager.mapper.TaskMapper;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.taskManager.repository.TaskRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    private final TaskMapper taskMapper;

    @Override
    public TaskDto getTaskById(Long id) {
        log.info("Getting task with id {}", id);

        return taskMapper.toDto(taskRepository.getReferenceById(id));
    }

    @Override
    public List<TaskDto> getAllTasks() {
        log.info("Getting all tasks");
        return taskRepository.findAll().stream().map(taskMapper::toDto).toList();
    }
}

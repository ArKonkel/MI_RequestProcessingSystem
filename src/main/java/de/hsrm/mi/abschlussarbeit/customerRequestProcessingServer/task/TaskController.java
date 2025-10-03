package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.task;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    ResponseEntity<List<TaskDto>> getAllTasks() {
        log.info("REST request to get all tasks");
        List<TaskDto> tasks = taskService.getAllTasks();

        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{taskId}")
    ResponseEntity<TaskDto> getTask(@PathVariable Long taskId) {
        log.info("REST request to get task with id {}", taskId);

        TaskDto task = taskService.getTaskDtoById(taskId);

        return ResponseEntity.ok(task);
    }

    @PatchMapping("/{taskId}")
    ResponseEntity<TaskDto> updateTask(@PathVariable Long taskId, @RequestBody UpdateTaskDto dto) {
        log.info("REST request to update task {}", taskId);
        TaskDto task = taskService.updateTask(taskId, dto);

        return ResponseEntity.ok(task);
    }

    @PostMapping("/{taskId}/workingTime")
    ResponseEntity<Void> addWorkingTime(@PathVariable Long taskId, @RequestParam BigDecimal workingTime, @RequestParam WorkingTimeUnit unit) {
        log.info("REST request to add working time {} {} to task {}", workingTime, unit, taskId);

        taskService.addWorkingTime(taskId, workingTime, unit);

        return ResponseEntity.ok().build();
    }

    @PostMapping
    ResponseEntity<TaskDto> createTask(@Valid @RequestBody TaskCreateDto createDto){
        log.info("REST request to create task {}", createDto);

        TaskDto createdTask = taskService.createTask(createDto);
        URI location = URI.create("/api/tasks/" + createdTask.processItem().getId());

        return ResponseEntity.created(location).body(createdTask);
    }

}

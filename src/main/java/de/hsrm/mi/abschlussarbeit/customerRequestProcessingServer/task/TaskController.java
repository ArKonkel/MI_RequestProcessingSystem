package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.task;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/task")
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    ResponseEntity<List<TaskDto>> getAllTasks() {
        log.info("REST request to get all tasks");
        List<TaskDto> tasks = taskService.getAllTasks();

        return ResponseEntity.ok(tasks);
    }

    @PostMapping("/{taskId}/workingTime")
    ResponseEntity<Void> addWorkingTime(@PathVariable Long taskId, @RequestParam BigDecimal workingTime, @RequestParam WorkingTimeUnit unit) {
        log.info("REST request to add working time {} {} to task {}", workingTime, unit, taskId);

        taskService.addWorkingTime(taskId, workingTime, unit);

        return ResponseEntity.ok().build();
    }

}

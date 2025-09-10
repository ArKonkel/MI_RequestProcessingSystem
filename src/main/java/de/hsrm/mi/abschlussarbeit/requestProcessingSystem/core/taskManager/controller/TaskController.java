package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.taskManager.controller;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.taskManager.dto.TaskDto;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.taskManager.service.TaskService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}

package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.resourceCapacityPlanner.controller;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.resourceCapacityPlanner.dto.MatchingEmployeeForTaskDto;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.resourceCapacityPlanner.service.ResourceCapacityService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/capacity")
public class ResourceCapacityController {

    private final ResourceCapacityService resourceCapacityService;


    @GetMapping("/{taskId}")
    public ResponseEntity<MatchingEmployeeForTaskDto> getMatchingEmployees(@PathVariable Long taskId) {
        log.info("REST request to get best matches for task {}", taskId);
        MatchingEmployeeForTaskDto matches = resourceCapacityService.findBestMatches(taskId);

        return ResponseEntity.ok(matches);
    }

}

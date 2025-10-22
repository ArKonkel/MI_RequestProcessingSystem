package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.capacity;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/capacity")
@PreAuthorize("hasAnyRole('ADMIN', 'CAPACITY_PLANNER')")
public class CapacityController {

    private final CapacityService capacityService;


    @GetMapping("/{taskId}")
    public ResponseEntity<MatchingEmployeeCapacitiesDto> calculateMatchingEmployees(@PathVariable Long taskId) {
        log.info("REST request to get best matches for task {}", taskId);
        MatchingEmployeeCapacitiesDto matches = capacityService.findBestMatchesForTask(taskId);

        return ResponseEntity.ok(matches);
    }

    @GetMapping("/{taskId}/{employeeId}")
    public ResponseEntity<MatchingEmployeeCapacitiesDto> calculateFreeCapacities(@PathVariable Long taskId, @PathVariable Long employeeId) {
        log.info("REST request to get free capacities for task {} and employee {}", taskId, employeeId);

        MatchingEmployeeCapacitiesDto match = capacityService.calculateFreeCapacities(taskId, employeeId);

        return ResponseEntity.ok(match);
    }

    @PostMapping("/assign/{taskId}")
    public ResponseEntity<Void> assignTaskToEmployee(@RequestBody CalculatedCapacitiesOfMatchDto selectedMatch, @PathVariable Long taskId) {
        log.info("REST request to assign assign employee {} to task {}", selectedMatch.getEmployee().id(), taskId);

        capacityService.assignMatchToEmployee(taskId, selectedMatch);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteCapacities(@PathVariable Long taskId) {
        log.info("REST request to delete capacities for task {}", taskId);

        capacityService.deleteCapacities(taskId);

        return ResponseEntity.ok().build();
    }

}

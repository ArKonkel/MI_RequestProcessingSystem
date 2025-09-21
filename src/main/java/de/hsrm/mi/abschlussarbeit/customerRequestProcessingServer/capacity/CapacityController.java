package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.capacity;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/capacity")
public class CapacityController {

    private final CapacityService capacityService;


    @GetMapping("/{taskId}")
    public ResponseEntity<MatchingEmployeeForTaskDto> getMatchingEmployees(@PathVariable Long taskId) {
        log.info("REST request to get best matches for task {}", taskId);
        MatchingEmployeeForTaskDto matches = capacityService.findBestMatches(taskId);

        return ResponseEntity.ok(matches);
    }

    @PostMapping("/assign/{taskId}")
    public ResponseEntity<Void> assignTaskToEmployee(@RequestBody MatchCalculationResultDto selectedMatch, @PathVariable Long taskId) {
        log.info("REST request to assign assign employee {} to task {}", selectedMatch.employee().id(), taskId);

        capacityService.assignMatchToEmployee(taskId, selectedMatch);

        return ResponseEntity.ok().build();
    }

}

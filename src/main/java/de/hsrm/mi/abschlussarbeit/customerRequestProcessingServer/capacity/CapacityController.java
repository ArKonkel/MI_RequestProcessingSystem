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
    public ResponseEntity<MatchingEmployeeForTaskVO> getMatchingEmployees(@PathVariable Long taskId) {
        log.info("REST request to get best matches for task {}", taskId);
        MatchingEmployeeForTaskVO matches = capacityService.findBestMatches(taskId);
        //TODO should be no VO, should be a DTO.

        return ResponseEntity.ok(matches);
    }

    @PostMapping("/assign/{taskId}")
    public ResponseEntity<Void> assignTaskToEmployee(@RequestBody MatchCalculationResultVO selectedMatch, @PathVariable Long taskId) {
        log.info("REST request to assign assign employee {} to task {}", selectedMatch.employee().getId(), taskId);

        capacityService.assignMatchToEmployee(taskId, selectedMatch);

        return ResponseEntity.ok().build();
    }

}

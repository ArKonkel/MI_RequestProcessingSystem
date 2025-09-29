package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.processItem;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/processItem")
public class ProcessItemController {

    private final ProcessItemService processItemService;

    @PostMapping("/{processItemId}/assign/{userId}")
    public ResponseEntity<Void> assignProcessItemToUser(
            @PathVariable Long processItemId,
            @PathVariable Long userId) {
        log.info("REST request to assign process item {} to employee {}", processItemId, userId);

        processItemService.assignProcessItemToUser(processItemId, userId);

        return ResponseEntity.ok().build();
    }

}

package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.expertise;

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
@RequestMapping("/expertise")
public class ExpertiseController {

    private final ExpertiseService expertiseService;

    @GetMapping
    public ResponseEntity<List<ExpertiseDto>> getAllExpertises() {
        log.info("REST request to get all expertises");

        return ResponseEntity.ok(expertiseService.getAllExpertises());
    }
}

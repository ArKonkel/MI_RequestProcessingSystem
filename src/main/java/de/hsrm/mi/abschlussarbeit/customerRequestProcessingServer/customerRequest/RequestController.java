package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.customerRequest;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/requests")
public class RequestController {

    private final RequestService requestService;

    @GetMapping("/{id}")
    public ResponseEntity<RequestDto> getRequestById(@PathVariable Long id) {
        log.info("REST request to get request with id {}", id);
        RequestDto request = requestService.getRequestById(id);

        return ResponseEntity.ok(request);
    }

    @GetMapping
    public ResponseEntity<List<RequestDto>> getAllRequests() {
        log.info("REST request to get all requests");
        List<RequestDto> requests = requestService.getAllRequests();

        return ResponseEntity.ok(requests);
    }

    @PostMapping
    ResponseEntity<RequestDto> createRequest(@Valid @RequestBody RequestCreateDto requestDto) {
        log.info("REST request to create request {}", requestDto);

        RequestDto createdRequest = requestService.createRequest(requestDto);
        URI location = URI.create("/api/requests/" + createdRequest.processItem().id());

        return ResponseEntity.created(location).body(createdRequest);
    }
}

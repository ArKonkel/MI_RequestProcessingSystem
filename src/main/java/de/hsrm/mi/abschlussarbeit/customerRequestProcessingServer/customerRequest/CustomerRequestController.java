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
public class CustomerRequestController {

    private final CustomerRequestService customerRequestService;

    @GetMapping("/{id}")
    public ResponseEntity<CustomerRequestDto> getRequestById(@PathVariable Long id) {
        log.info("REST request to get request with id {}", id);
        CustomerRequestDto request = customerRequestService.getRequestById(id);

        return ResponseEntity.ok(request);
    }

    @GetMapping
    public ResponseEntity<List<CustomerRequestDto>> getAllRequests(@RequestParam(required = false) CustomerRequestStatus status) {
        log.info("REST request to get all requests");
        List<CustomerRequestDto> requests;

        if (status != null) {
            requests = customerRequestService.getRequestsByStatus(status);
        } else {
            requests = customerRequestService.getAllRequests();
        }

        return ResponseEntity.ok(requests);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<CustomerRequestDto>> getRequestFromCustomer(@PathVariable Long customerId) {
        log.info("REST request to get all requests from customer {}", customerId);

        List<CustomerRequestDto> requests = customerRequestService.getRequestsByCustomerId(customerId);
        return ResponseEntity.ok(requests);
    }

    @PostMapping
    ResponseEntity<CustomerRequestDto> createRequest(@Valid @RequestBody CustomerRequestCreateDto requestDto) {
        log.info("REST request to create request {}", requestDto);

        CustomerRequestDto createdRequest = customerRequestService.createRequest(requestDto);
        URI location = URI.create("/api/requests/" + createdRequest.processItem.getId());

        return ResponseEntity.created(location).body(createdRequest);
    }
}

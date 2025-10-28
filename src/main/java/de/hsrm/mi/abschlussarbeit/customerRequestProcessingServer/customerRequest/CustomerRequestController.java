package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.customerRequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.util.List;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/requests")
@PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER', 'CUSTOMER_REQUEST_REVISER', 'CAPACITY_PLANNER', 'TASK_REVISER', 'PROJECT_PLANNER')")
public class CustomerRequestController {

    private final CustomerRequestService customerRequestService;

    @GetMapping("/{id}")
    @PreAuthorize("!hasRole('CUSTOMER')")
    public ResponseEntity<CustomerRequestDto> getRequestById(@PathVariable Long id) {
        log.info("REST request to get request with id {}", id);
        CustomerRequestDto request = customerRequestService.getRequestDtoById(id);

        return ResponseEntity.ok(request);
    }

    @GetMapping
    @PreAuthorize("!hasRole('CUSTOMER')")
    public ResponseEntity<List<CustomerRequestDto>> getAllRequests() {
        log.info("REST request to get all requests");

        List<CustomerRequestDto> requests = customerRequestService.getAllRequests();

        return ResponseEntity.ok(requests);
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<List<CustomerRequestDto>> getRequestOfCustomer(@PathVariable Long customerId) {
        log.info("REST request to get all requests from customer {}", customerId);

        List<CustomerRequestDto> requests = customerRequestService.getRequestsByCustomerId(customerId);
        return ResponseEntity.ok(requests);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER_REQUEST_REVISER', 'PROJECT_PLANNER')")
    public ResponseEntity<CustomerRequestDto> updateCustomerRequest(@PathVariable Long id, @RequestBody UpdateCustomerRequestDto dto) {
        log.info("REST request to update customerRequest {}", id);

        CustomerRequestDto requestDto = customerRequestService.updateCustomerRequest(id, dto);

        return ResponseEntity.ok(requestDto);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<CustomerRequestDto> createRequest(
            @RequestPart("request") @Valid String requestJson,
            @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments //attachments cannot be added to DTO. That's why it is a request part.
    ) throws IOException {
        log.info("REST request to create customer request with requestJson {}", requestJson);

        CustomerRequestCreateDto requestDto = new ObjectMapper()
                .readValue(requestJson, CustomerRequestCreateDto.class);

        CustomerRequestDto createdRequest = customerRequestService.createRequest(requestDto, attachments);
        URI location = URI.create("/api/requests/" + createdRequest.getProcessItem().getId());
        return ResponseEntity.created(location).body(createdRequest);
    }

}

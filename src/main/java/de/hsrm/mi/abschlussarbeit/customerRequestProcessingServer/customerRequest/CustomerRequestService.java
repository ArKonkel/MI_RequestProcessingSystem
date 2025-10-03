package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.customerRequest;

import java.util.List;

public interface CustomerRequestService {

    CustomerRequestDto createRequest(CustomerRequestCreateDto request);

    List<CustomerRequestDto> getAllRequests();

    List<CustomerRequestDto> getRequestsByCustomerId(Long customerId);

    CustomerRequestDto getRequestDtoById(Long id);

    CustomerRequestDto updateCustomerRequest(Long id, UpdateCustomerRequestDto updateDto);

    CustomerRequest getRequestById(Long id);

    boolean isRequestReadyForProcessing(Long requestId);
}

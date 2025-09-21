package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.customerRequest;

import java.util.List;

public interface CustomerRequestService {

    CustomerRequestDto createRequest(CustomerRequestCreateDto request);

    List<CustomerRequestDto> getAllRequests();

    CustomerRequestDto getRequestById(Long id);
}

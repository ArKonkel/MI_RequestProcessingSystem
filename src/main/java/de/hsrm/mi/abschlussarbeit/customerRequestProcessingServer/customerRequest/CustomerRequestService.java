package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.customerRequest;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface CustomerRequestService {

    CustomerRequestDto createRequest(CustomerRequestCreateDto request, List<MultipartFile> attachments) throws IOException;

    List<CustomerRequestDto> getAllRequests();

    List<CustomerRequestDto> getRequestsByCustomerId(Long customerId);

    CustomerRequestDto getRequestDtoById(Long id);

    CustomerRequestDto updateCustomerRequest(Long id, UpdateCustomerRequestDto updateDto);

    CustomerRequest getRequestById(Long id);

    boolean isRequestReadyForProcessing(Long requestId);
}

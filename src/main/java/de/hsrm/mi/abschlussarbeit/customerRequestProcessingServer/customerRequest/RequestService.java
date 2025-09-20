package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.customerRequest;

import java.util.List;

public interface RequestService {

    RequestDto createRequest(RequestCreateDto request);

    List<RequestDto> getAllRequests();

    RequestDto getRequestById(Long id);
}

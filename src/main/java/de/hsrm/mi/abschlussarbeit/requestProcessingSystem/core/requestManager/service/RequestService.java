package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.requestManager.service;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.requestManager.dto.RequestCreateDto;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.requestManager.dto.RequestDto;

import java.util.List;

public interface RequestService {

    RequestDto createRequest(RequestCreateDto request);

    List<RequestDto> getAllRequests();

    RequestDto getRequestById(Long id);
}

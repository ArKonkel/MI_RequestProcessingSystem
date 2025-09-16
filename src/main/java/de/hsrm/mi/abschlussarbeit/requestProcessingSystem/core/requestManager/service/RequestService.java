package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.requestManager.service;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.requestManager.dto.CreateRequestDto;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.requestManager.dto.RequestDto;

public interface RequestService {

    RequestDto createRequest(CreateRequestDto request);
}

package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.requestManager.service;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.requestManager.dto.RequestCreateDto;

public interface ValidationService {

    void validateRequestCreation(RequestCreateDto requestCreateDto);
}

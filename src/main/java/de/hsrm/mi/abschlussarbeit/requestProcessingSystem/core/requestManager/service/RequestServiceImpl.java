package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.requestManager.service;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.requestManager.dto.RequestCreateDto;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.requestManager.dto.RequestDto;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.requestManager.entity.Request;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.requestManager.mapper.RequestMapper;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.requestManager.repository.RequestRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@AllArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;

    private final RequestMapper requestMapper;

    private final ValidationService validationService;

    @Override
    public RequestDto createRequest(RequestCreateDto request) {
        log.info("Creating request {}", request);

        validationService.validateRequestCreation(request);

        Request requestEntity = requestMapper.toEntity(request);
        requestEntity.setCreationDate(LocalDateTime.now()); //set creation date to current date

        Request savedRequest = requestRepository.save(requestEntity);

        return requestMapper.toDto(savedRequest);
    }
}

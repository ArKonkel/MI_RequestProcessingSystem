package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.customerRequest;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.globalExceptionHandler.NotFoundException;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.customer.CustomerRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;

    private final CustomerRepository customerRepository;

    private final RequestMapper requestMapper;

    private final ValidationService validationService;

    @Override
    public RequestDto createRequest(RequestCreateDto request) {
        log.info("Creating request {}", request);

        if (!customerRepository.existsById(request.getCustomerId())) {
            throw new NotFoundException("Customer with id " + request.getCustomerId() + " not found.");
        }

        validationService.validateRequestCreation(request);

        Request requestEntity = requestMapper.toEntity(request);
        requestEntity.setCreationDate(LocalDateTime.now()); //set creation date to current date
        requestEntity.setStatus(RequestStatus.RECEIVED);

        //set customer
        requestEntity.setCustomer(customerRepository.getReferenceById(request.getCustomerId()));

        Request savedRequest = requestRepository.save(requestEntity);

        return requestMapper.toDto(savedRequest);
    }

    @Override
    public List<RequestDto> getAllRequests() {
        log.info("Getting all requests");
        return requestRepository.findAll().stream().map(requestMapper::toDto).toList();
    }

    @Override
    public RequestDto getRequestById(Long id) {
        log.info("Getting request with id {}", id);
        return requestMapper.toDto(requestRepository.getReferenceById(id));
    }
}

package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.customerRequest;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.customer.CustomerRepository;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.globalExceptionHandler.NotFoundException;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.shared.TimeUnit;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class CustomerRequestServiceImpl implements CustomerRequestService {

    private final CustomerRequestRepository requestRepository;

    private final CustomerRepository customerRepository;

    private final CustomerRequestMapper requestMapper;

    private final ValidationService validationService;

    @Override
    public CustomerRequestDto createRequest(CustomerRequestCreateDto request) {
        log.info("Creating request {}", request);

        if (!customerRepository.existsById(request.getCustomerId())) {
            throw new NotFoundException("Customer with id " + request.getCustomerId() + " not found.");
        }

        validationService.validateRequestCreation(request);

        CustomerRequest requestEntity = requestMapper.toEntity(request);
        requestEntity.setCreationDate(Instant.now()); //set creation date to current date
        requestEntity.setStatus(CustomerRequestStatus.RECEIVED);
        requestEntity.setChargeable(Chargeable.NOT_DETERMINED);
        requestEntity.setScopeUnit(TimeUnit.HOUR);

        //set customer
        requestEntity.setCustomer(customerRepository.getReferenceById(request.getCustomerId()));

        CustomerRequest savedRequest = requestRepository.save(requestEntity);

        return requestMapper.toDto(savedRequest);
    }

    @Override
    public List<CustomerRequestDto> getAllRequests() {
        log.info("Getting all requests");
        return requestRepository.findAll().stream().map(requestMapper::toDto).toList();
    }

    @Override
    public CustomerRequestDto getRequestById(Long id) {
        log.info("Getting request with id {}", id);
        return requestMapper.toDto(requestRepository.getReferenceById(id));
    }
}

package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.customerRequest;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.customer.CustomerService;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.globalExceptionHandler.NotAllowedException;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.globalExceptionHandler.NotFoundException;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.integration.outlook.graphTypes.ItemBody;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.integration.outlook.graphTypes.Message;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.integration.outlook.graphTypes.Recipient;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.integration.outlook.graphTypes.SendMailRequest;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.mail.EmailAddress;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.mail.MailService;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.notification.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Slf4j
@AllArgsConstructor
public class CustomerRequestServiceImpl implements CustomerRequestService {

    private final NotificationService notificationService;

    private final CustomerRequestRepository customerRequestRepository;

    private final CustomerService customerService;

    private final CustomerRequestMapper requestMapper;

    private final MailService mailService;

    /**
     * Creates a new customer request, saves it to the repository, sends notifications, and triggers email actions.
     *
     * @param request the data transfer object containing the details of the customer request to be created
     * @return the data transfer object of the saved customer request
     */
    @Override
    public CustomerRequestDto createRequest(CustomerRequestCreateDto request) {
        log.info("Creating request {}", request);

        CustomerRequest requestEntity = requestMapper.toEntity(request);
        requestEntity.setCustomer(customerService.getCustomerById(request.getCustomerId()));

        CustomerRequest savedRequest = customerRequestRepository.save(requestEntity);

        //Send mails
        SendMailRequest mailRequest = parseCustomerRequestToMail(savedRequest, request.getToRecipients());
        mailService.sendMails(mailRequest, savedRequest.getCustomer().getEmail());

        notificationService.sendChangeNotification(
                new ChangeNotificationEvent(savedRequest.getId(), ChangeType.CREATED, TargetType.CUSTOMER_REQUEST));

        notificationService.sendUserNotification(new UserNotificationEvent(UserNotificationType.INCOMING_REQUEST, savedRequest.getId(), savedRequest.getTitle(),
                List.of(), "New CustomerRequest arrived.", Instant.now(), TargetType.CUSTOMER_REQUEST)
        );

        return requestMapper.toDto(savedRequest);
    }

    /**
     * Retrieves all customer requests, ordered by creation date and ID in descending order.
     *
     * @return a list of customer requests as data transfer objects, sorted by creation date and ID in descending order
     */
    @Override
    @Transactional(readOnly = true) //Needed because fetching blob
    public List<CustomerRequestDto> getAllRequests() {
        log.info("Getting all requests");

        return customerRequestRepository.
                findAllByOrderByCreationDateDescIdDesc()
                .stream()
                .map(requestMapper::toDto)
                .toList();
    }


    /**
     * Retrieves all customer requests for a specific customer, ordered by creation date in descending order.
     *
     * @param customerId the ID of the customer whose requests are to be retrieved
     * @return a list of customer requests as data transfer objects, sorted by creation date in descending order
     */
    @Override
    @Transactional(readOnly = true)
    public List<CustomerRequestDto> getRequestsByCustomerId(Long customerId) {
        log.info("Getting all requests for customer {}", customerId);

        List<CustomerRequestDto> requestDtos = customerRequestRepository
                .findByCustomerId(customerId, Sort.by(Sort.Direction.DESC, "creationDate"))
                .stream()
                .map(requestMapper::toDto)
                .toList();

        //Sort comments desc timeStamp
        return requestDtos;
    }


    /**
     * Retrieves a customer request by its ID and converts it to a data transfer object.
     *
     * @param id the ID of the customer request to retrieve
     * @return the data transfer object of the retrieved customer request
     * @throws NotFoundException if no customer request is found with the given ID
     */
    @Override
    @Transactional(readOnly = true)
    public CustomerRequestDto getRequestDtoById(Long id) {
        log.info("Getting request with id {}", id);

        return requestMapper.toDto(customerRequestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Request with id " + id + " not found")));
    }

    /**
     * Updates an existing customer request based on the specified update data.
     *
     * @param id the unique identifier of the customer request to update
     * @param updateDto the data containing the updates to apply to the customer request
     * @return a DTO representing the updated customer request
     * @throws NotFoundException if no customer request with the given id is found
     * @throws NotAllowedException if modifications to the classification as a project are not permitted
     */
    @Override
    @Transactional
    public CustomerRequestDto updateCustomerRequest(Long id, UpdateCustomerRequestDto updateDto) {
        log.info("Updating request with id {}, {}", id, updateDto);

        CustomerRequest request = customerRequestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Request with id " + id + " not found"));

        if (updateDto.getPriority() != null) {
            request.setPriority(updateDto.getPriority());
        }

        if (updateDto.getEstimatedScope() != null) {
            request.setEstimatedScope(updateDto.getEstimatedScope());
        }

        if (updateDto.getScopeUnit() != null) {
            request.setScopeUnit(updateDto.getScopeUnit());
        }

        if (updateDto.getStatus() != null) {
            request.setStatus(updateDto.getStatus());
        }

        if (updateDto.getChargeable() != null) {
            request.setChargeable(updateDto.getChargeable());
        }

        if (updateDto.getCategory() != null) {
            request.setCategory(updateDto.getCategory());
        }

        if (updateDto.getTitle() != null) {
            request.setTitle(updateDto.getTitle());
        }

        if (updateDto.getDescription() != null) {
            request.setDescription(updateDto.getDescription());
        }

        if (updateDto.getClassifiedAsProject() != null) {
            if (request.getClassifiedAsProject().equals(IsProjectClassification.YES) && !request.getProjects().isEmpty()) {
                throw new NotAllowedException("Customer Request has already attached Projects. No change allowed.");
            }

            request.setClassifiedAsProject(updateDto.getClassifiedAsProject());
        }

        CustomerRequest savedRequest = customerRequestRepository.save(request);

        notificationService.sendChangeNotification(new ChangeNotificationEvent(savedRequest.getId(), ChangeType.UPDATED, TargetType.CUSTOMER_REQUEST));

        return requestMapper.toDto(savedRequest);
    }

    /**
     * Retrieves a customer request by its ID.
     *
     * @param id the unique identifier of the customer request to retrieve
     * @return the customer request associated with the given ID
     * @throws NotFoundException if no customer request is found with the specified ID
     */
    @Override
    @Transactional(readOnly = true)
    public CustomerRequest getRequestById(Long id) {
        log.info("Getting customer request with id {}", id);

        return customerRequestRepository.findById(id).orElseThrow(() -> new NotFoundException("Request with id " + id + " not found"));
    }


    /**
     * Checks whether the specified customer request is ready for processing.
     * A request is considered ready for processing if its status is
     * either WAITING_FOR_PROCESSING or IN_PROCESS.
     *
     * @param requestId the unique identifier of the customer request to check
     * @return true if the request is ready for processing, false otherwise
     * @throws NoSuchElementException if no customer request is found with the specified ID
     */
    @Override
    public boolean isRequestReadyForProcessing(Long requestId) {
        CustomerRequest request = customerRequestRepository.findById(requestId).orElseThrow(() ->
                new NoSuchElementException("Request with id " + requestId + " not found"));

        return request.getStatus().equals(CustomerRequestStatus.WAITING_FOR_PROCESSING) || request.getStatus().equals(CustomerRequestStatus.IN_PROCESS);
    }


    /**
     * Converts a CustomerRequest to a SendMailRequest.
     *
     * @param customerRequest to convert
     * @param emailAddresses  to send mail to
     * @return converted SendMailRequest
     */
    private SendMailRequest parseCustomerRequestToMail(CustomerRequest customerRequest, List<EmailAddress> emailAddresses) {
        List<Recipient> toRecipients = new ArrayList<>();

        for (EmailAddress emailAddress : emailAddresses) {
            toRecipients.add(new Recipient(
                    new de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.integration.outlook.graphTypes.EmailAddress(
                            emailAddress.address()
                    )
            ));
        }

        // build mailRequest
        SendMailRequest mailRequest = new SendMailRequest(
                new Message(
                        customerRequest.getCategory().toString() + ":" + customerRequest.getTitle(),
                        new ItemBody("Text", customerRequest.getDescription()),
                        toRecipients
                )
        );
        return mailRequest;
    }
}

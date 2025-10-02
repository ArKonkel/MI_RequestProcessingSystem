package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.customerRequest;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.customer.CustomerService;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.globalExceptionHandler.NotFoundException;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.integration.outlook.graphTypes.ItemBody;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.integration.outlook.graphTypes.Message;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.integration.outlook.graphTypes.Recipient;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.integration.outlook.graphTypes.SendMailRequest;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.mail.EmailAddress;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.mail.MailService;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.notification.ChangeNotificationEvent;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.notification.ChangeType;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.notification.NotificationService;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.notification.TargetType;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

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

        return requestMapper.toDto(savedRequest);
    }

    @Override
    public List<CustomerRequestDto> getAllRequests() {
        log.info("Getting all requests");

        List<CustomerRequestDto> requestDtos = customerRequestRepository
                .findAll(Sort.by(Sort.Direction.DESC, "creationDate"))
                .stream()
                .map(requestMapper::toDto)
                .toList();

        return requestDtos;
    }


    @Override
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


    @Override
    public CustomerRequestDto getRequestById(Long id) {
        log.info("Getting request with id {}", id);

        return requestMapper.toDto(customerRequestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Request with id " + id + " not found")));
    }

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

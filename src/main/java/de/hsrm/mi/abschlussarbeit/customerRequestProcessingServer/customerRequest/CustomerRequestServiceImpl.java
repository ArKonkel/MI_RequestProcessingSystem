package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.customerRequest;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.customer.CustomerService;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.integration.outlook.graphTypes.ItemBody;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.integration.outlook.graphTypes.Message;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.integration.outlook.graphTypes.Recipient;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.integration.outlook.graphTypes.SendMailRequest;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.mail.EmailAddress;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.mail.MailService;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.shared.TimeUnit;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class CustomerRequestServiceImpl implements CustomerRequestService {

    private final CustomerRequestRepository requestRepository;

    private final CustomerService customerService;

    private final CustomerRequestMapper requestMapper;

    private final MailService mailService;

    @Override
    public CustomerRequestDto createRequest(CustomerRequestCreateDto request) {
        log.info("Creating request {}", request);

        CustomerRequest requestEntity = requestMapper.toEntity(request);
        requestEntity.setCreationDate(Instant.now()); //set creation date to current date
        requestEntity.setStatus(CustomerRequestStatus.RECEIVED);
        requestEntity.setChargeable(Chargeable.NOT_DETERMINED);
        requestEntity.setScopeUnit(TimeUnit.HOUR);

        //set customer
        requestEntity.setCustomer(customerService.getCustomerById(request.getCustomerId()));

        CustomerRequest savedRequest = requestRepository.save(requestEntity);

        //Send mails
        SendMailRequest mailRequest = parseCustomerRequestToMail(savedRequest, request.getToRecipients());
        mailService.sendMails(mailRequest, savedRequest.getCustomer().getEmail(), request.getToRecipients());

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

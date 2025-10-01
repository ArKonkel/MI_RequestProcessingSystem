package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.customerRequest;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.comment.CommentDto;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.comment.CommentMapper;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.customer.CustomerService;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.integration.outlook.graphTypes.ItemBody;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.integration.outlook.graphTypes.Message;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.integration.outlook.graphTypes.Recipient;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.integration.outlook.graphTypes.SendMailRequest;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.mail.EmailAddress;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.mail.MailService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Slf4j
@AllArgsConstructor
public class CustomerRequestServiceImpl implements CustomerRequestService {

    private final CustomerRequestRepository customerRequestRepository;

    private final CustomerService customerService;

    private final CustomerRequestMapper requestMapper;

    private final CommentMapper commentMapper;

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

        return requestMapper.toDto(savedRequest);
    }

    @Override
    public List<CustomerRequestDto> getAllRequests() {
        log.info("Getting all requests");
        return customerRequestRepository.findAll().stream().map(requestMapper::toDto).toList();
    }
/*
    @Override
    public List<CustomerRequestDto> getRequestsByCustomerId(Long customerId) {
        log.info("Getting all requests for customer {}", customerId);

        //TODO sollte sortiert sein
        val requestDtos = customerRequestRepository.findByCustomerIdOrderByCreationDateDesc(customerId)
                .stream().map(requestMapper::toDto).toList();

        for (CustomerRequestDto requestDto : requestDtos) {
            val comments = requestDto.processItem().comments();

            comments.stream().sorted(Comparator.comparing(commentDto -> commentDto.timeStamp()).reversed()).collect(Collectors.toList());

        }

        return requestDtos;
    }

 */

    @Override
    public List<CustomerRequestDto> getRequestsByCustomerId(Long customerId) {
        log.info("Getting all requests for customer {}", customerId);

        // Alle Requests als DTOs holen (ohne Kommentar-Sortierung)
        List<CustomerRequestDto> requestDtos = customerRequestRepository.findByCustomerIdOrderByCreationDateDesc(customerId)
                .stream()
                .map(requestMapper::toDto)
                .toList();

        //Sort comments desc timeStamp
        for (CustomerRequestDto requestDto : requestDtos) {
            List<CommentDto> comments = requestDto.processItem.getComments();

            // Neue sortierte Liste erzeugen
            List<CommentDto> sortedComments = comments.stream()
                    .sorted(Comparator.comparing(CommentDto::timeStamp).reversed()).toList();

            // Sorted Comments im DTO setzen - Dazu brauchst du einen Setter oder ein neues DTO.
            // Beispiel mit mutierbarem DTO:
            requestDto.processItem.setComments(sortedComments);

            // Falls die ProcessItem immutable ist, musst du ggf. ein neues ProcessItemDto erstellen und ersetzen
        }

        return requestDtos;
    }



    @Override
    public CustomerRequestDto getRequestById(Long id) {
        log.info("Getting request with id {}", id);
        return requestMapper.toDto(customerRequestRepository.getReferenceById(id));
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

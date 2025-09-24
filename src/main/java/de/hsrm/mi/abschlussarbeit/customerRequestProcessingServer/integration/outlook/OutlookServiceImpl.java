package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.integration.outlook;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.calendar.CalendarService;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.customerRequest.CustomerRequest;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.integration.outlook.graphTypes.*;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.mail.EmailAddress;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.mail.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class OutlookServiceImpl implements MailService, OutlookCalendarService {

    private final CalendarService calendarService;

    @Value("${outlook.api.url}")
    private String outlookURL;

    @Autowired
    public OutlookServiceImpl(CalendarService calendarService) {
        this.calendarService = calendarService;
    }

    /**
     * Converts a CustomerRequest to a SendMailRequest and sends it to the given email addresses.
     *
     * @param customerRequest to send mail from
     * @param emailAddresses to send the mail to
     */
    @Override
    public void sendMails(CustomerRequest customerRequest, List<EmailAddress> emailAddresses) {
        log.info("Sending mail from {} to {} with title {}", customerRequest.getCustomer().getEmail(), emailAddresses, customerRequest.getTitle());
        String senderMail = customerRequest.getCustomer().getEmail();
        String path = outlookURL + "v1.0/users/" + senderMail + "/sendMail";

        if (senderMail == null || senderMail.isEmpty()) {

            //TODO throw error then?
            log.error("Customer {} has no mail.", customerRequest.getCustomer().getId());
            return;
        }

        if (emailAddresses.isEmpty()) {
            return;
        }

        SendMailRequest mailRequest = customerRequestToMailRequest(customerRequest, emailAddresses);


        WebClient webClient = WebClient.builder()
                .baseUrl(path)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        try {
            webClient.post()
                    .bodyValue(mailRequest)
                    .retrieve()
                    .toBodilessEntity()
                    .block();

            log.info("Mail sent successfully from {}", senderMail);
        } catch (WebClientResponseException e) {
            log.error("Error sending mail: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage());
        }
    }


    @Override
    public void initCalendarOfEmployee(Long employeeId) {

    }

    @Override
    public OutlookCalendarViewResponse fetchCalendarEvents(String employeeMail, OffsetDateTime start, OffsetDateTime end) {
        log.info("Fetching calendar events for employee {} from {} to {}", employeeMail, start, end);

        if (employeeMail == null || employeeMail.isEmpty()) {
            log.error("Employee mail is null or empty");
            return null;
        }

        WebClient webClient = WebClient.builder()
                .baseUrl(outlookURL)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        try {
            OutlookCalendarViewResponse response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("v1.0/users/{employeeMail}/calendar/calendarView")
                            .queryParam("start", start.toString())
                            .queryParam("end", end.toString())
                            .build(employeeMail))
                    .retrieve()
                    .bodyToMono(OutlookCalendarViewResponse.class)
                    .block();

            if (response == null || response.value() == null || response.value().isEmpty()) {
                log.info("No calendar events found for {}", employeeMail);
                return null;
            }

            log.info("Fetched {} calendar events for {}", response.value().size(), employeeMail);
            return response;

        } catch (Exception e) {
            log.error("Error fetching calendar events: {}", e.getMessage(), e);
            return null;
        }
    }


    /**
     * Converts a CustomerRequest to a SendMailRequest.
     *
     * @param customerRequest to convert
     * @param emailAddresses  to send mail to
     * @return converted SendMailRequest
     */
    private SendMailRequest customerRequestToMailRequest(CustomerRequest customerRequest, List<EmailAddress> emailAddresses) {
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

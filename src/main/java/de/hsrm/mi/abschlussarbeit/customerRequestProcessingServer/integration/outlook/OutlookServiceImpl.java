package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.integration.outlook;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.integration.outlook.graphTypes.OutlookCalendarViewResponse;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.integration.outlook.graphTypes.SendMailRequest;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.mail.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OutlookServiceImpl implements MailService, OutlookCalendarService {

    @Value("${outlook.api.url}")
    private String outlookURL;

    /**
     * Converts a CustomerRequest to a SendMailRequest and sends it to the given email addresses.
     *
     * @param senderMail to send mail from
     */
    @Override
    public void sendMails(SendMailRequest mail, String senderMail) {
        log.info("Sending mail from {} to {} with title {}",
                senderMail,
                mail.message().toRecipients().stream()
                        .map(recipient -> recipient.emailAddress().address())
                        .collect(Collectors.joining(", ")),
                mail.message().subject());

        String path = outlookURL + "v1.0/users/" + senderMail + "/sendMail";

        if (senderMail == null || senderMail.isEmpty()) {
            throw new IllegalArgumentException("Sender mail is null or empty");
        }

        if (mail.message().toRecipients().isEmpty()) {
            return;
        }

        WebClient webClient = WebClient.builder()
                .baseUrl(path)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        try {
            webClient.post()
                    .bodyValue(mail)
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
    public OutlookCalendarViewResponse fetchCalendarEvents(String employeeMail, OffsetDateTime start, OffsetDateTime end) {
        log.info("Fetching calendar events for employee {} from {} to {}", employeeMail, start, end);

        if (employeeMail == null || employeeMail.isEmpty()) {
            log.error("Employee mail is null or empty");

            throw new IllegalArgumentException("Employee mail is null or empty");
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

                //empty list
                return new OutlookCalendarViewResponse(new ArrayList<>());
            }

            log.info("Fetched {} calendar events for {}", response.value().size(), employeeMail);
            return response;

        } catch (Exception e) {
            log.error("Error fetching calendar events: {}", e.getMessage(), e);

            throw new RuntimeException("Error fetching calendar events: " + e.getMessage(), e);
        }
    }

}

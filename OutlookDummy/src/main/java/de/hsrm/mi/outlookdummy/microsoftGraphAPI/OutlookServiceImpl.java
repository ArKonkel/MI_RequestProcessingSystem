package de.hsrm.mi.outlookdummy.microsoftGraphAPI;

import de.hsrm.mi.outlookdummy.microsoftGraphAPI.types.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OutlookServiceImpl implements MailService, OutlookCalendarService {

    private final String PRINCIPAL_ONE_MAIL;
    private final String PRINCIPAL_TWO_MAIL;
    private final String PRINCIPAL_THREE_MAIL;

    public OutlookServiceImpl(
            @Value("${userPrincipalOne}") String principalOne,
            @Value("${userPrincipalTwo}") String principalTwo,
            @Value("${userPrincipalThree}") String principalThree) {
        this.PRINCIPAL_ONE_MAIL = principalOne.toLowerCase();
        this.PRINCIPAL_TWO_MAIL = principalTwo.toLowerCase();
        this.PRINCIPAL_THREE_MAIL = principalThree.toLowerCase();
    }

    @Override
    public void sendMail(String fromPrincipal, SendMailRequest request) {
        log.info("Sending mail from {} to {}", fromPrincipal,
                request.message().toRecipients().stream()
                        .map(recipient -> recipient.emailAddress().address())
                        .collect(Collectors.joining(", ")));
    }
    
    @Override
    public OutlookCalendarViewResponse getCalendarView(String userPrincipalName, String start, String end) {
        log.info("Getting calendar view of {}", userPrincipalName);

        ZoneId berlinZone = ZoneId.of("Europe/Berlin");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        // Zeitberechnungen. Immer von heute, um nicht anpassen zu müssen.
        String today09_00 = LocalDateTime.now(berlinZone).withHour(9).withMinute(0).withSecond(0).format(formatter);
        String today10_00 = LocalDateTime.now(berlinZone).withHour(10).withMinute(0).withSecond(0).format(formatter);
        String tomorrow14_00 = LocalDateTime.now(berlinZone).plusDays(1).withHour(14).withMinute(0).withSecond(0).format(formatter);
        String tomorrow15_30 = LocalDateTime.now(berlinZone).plusDays(1).withHour(15).withMinute(30).withSecond(0).format(formatter);

        String today11_00 = LocalDateTime.now(berlinZone).withHour(11).withMinute(0).withSecond(0).format(formatter);
        String today12_30 = LocalDateTime.now(berlinZone).withHour(12).withMinute(30).withSecond(0).format(formatter);
        String today16_00 = LocalDateTime.now(berlinZone).withHour(16).withMinute(0).withSecond(0).format(formatter);
        String today16_30 = LocalDateTime.now(berlinZone).withHour(16).withMinute(30).withSecond(0).format(formatter);

        String today13_00 = LocalDateTime.now(berlinZone).withHour(13).withMinute(0).withSecond(0).format(formatter);
        String today14_00 = LocalDateTime.now(berlinZone).withHour(14).withMinute(0).withSecond(0).format(formatter);
        String today17_30 = LocalDateTime.now(berlinZone).withHour(17).withMinute(30).withSecond(0).format(formatter);
        String today18_30 = LocalDateTime.now(berlinZone).withHour(18).withMinute(30).withSecond(0).format(formatter);

        // Event über merhere Tage
        String today22_00 = LocalDateTime.now(berlinZone).withHour(22).withMinute(0).withSecond(0).format(formatter);
        String tomorrow02_00 = LocalDateTime.now(berlinZone).plusDays(1).withHour(2).withMinute(0).withSecond(0).format(formatter);

        List<OutlookCalendarEvent> events;

        userPrincipalName = userPrincipalName.toLowerCase();

        if (userPrincipalName.equals(PRINCIPAL_ONE_MAIL)) {
            events = List.of(
                    new OutlookCalendarEvent(
                            "AAMkAGIAAAoZDOFAAA=",
                            "Team Meeting",
                            new DateTimeTimeZone(today09_00, "Europe/Berlin"),
                            new DateTimeTimeZone(today10_00, "Europe/Berlin"),
                            new ItemBody("Text", "Wöchentliches Teammeeting im Büro.")
                    ),
                    new OutlookCalendarEvent(
                            "AAMkAGIAAAoZDOFBBB=",
                            "Kundenpräsentation",
                            new DateTimeTimeZone(tomorrow14_00, "Europe/Berlin"),
                            new DateTimeTimeZone(tomorrow15_30, "Europe/Berlin"),
                            new ItemBody("HTML", "<b>Präsentation</b> beim Kunden X.")
                    )
            );
        } else if (userPrincipalName.equals(PRINCIPAL_TWO_MAIL)) {
            events = List.of(
                    new OutlookCalendarEvent(
                            "AAMkAGIAAAoZDOFCCC=",
                            "Projekt Kickoff",
                            new DateTimeTimeZone(today11_00, "Europe/Berlin"),
                            new DateTimeTimeZone(today12_30, "Europe/Berlin"),
                            new ItemBody("Text", "Projektstart mit Team und Stakeholdern.")
                    ),
                    new OutlookCalendarEvent(
                            "AAMkAGIAAAoZDOFDDD=",
                            "1:1 Gespräch",
                            new DateTimeTimeZone(today16_00, "Europe/Berlin"),
                            new DateTimeTimeZone(today16_30, "Europe/Berlin"),
                            new ItemBody("Text", "Individuelles Mitarbeitergespräch.")
                    ),
                    new OutlookCalendarEvent(
                            "AAMkAGIAAAoZDOFEEE=",
                            "Overnight Maintenance",
                            new DateTimeTimeZone(today22_00, "Europe/Berlin"),
                            new DateTimeTimeZone(tomorrow02_00, "Europe/Berlin"),
                            new ItemBody("Text", "Serverwartung über Nacht.")
                    )
            );
        } else if (userPrincipalName.equals(PRINCIPAL_THREE_MAIL)) {
            events = List.of(
                    new OutlookCalendarEvent(
                            "AAMkAGIAAAoZDOFFFF=",
                            "Code Review",
                            new DateTimeTimeZone(today13_00, "Europe/Berlin"),
                            new DateTimeTimeZone(today14_00, "Europe/Berlin"),
                            new ItemBody("Text", "Review der neuen API-Endpunkte.")
                    ),
                    new OutlookCalendarEvent(
                            "AAMkAGIAAAoZDOFGGG=",
                            "Feierabend-Termin",
                            new DateTimeTimeZone(today17_30, "Europe/Berlin"),
                            new DateTimeTimeZone(today18_30, "Europe/Berlin"),
                            new ItemBody("Text", "Afterwork mit Kollegen.")
                    )
            );
        } else {
            events = List.of();
        }

        return new OutlookCalendarViewResponse(events);
    }

}

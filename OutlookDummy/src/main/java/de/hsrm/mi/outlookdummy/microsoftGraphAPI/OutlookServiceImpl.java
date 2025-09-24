package de.hsrm.mi.outlookdummy.microsoftGraphAPI;

import de.hsrm.mi.outlookdummy.microsoftGraphAPI.types.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
public class OutlookServiceImpl implements MailService, OutlookCalendarService {

    private final String MAX_MAIL;
    private final String SABINE_MAIL;
    private final String FREDDY_MAIL;

    public OutlookServiceImpl(
            @Value("${userPrincipalOne}") String maxMail,
            @Value("${userPrincipalTwo}") String sabineMail,
            @Value("${userPrincipalThree}") String freddyMail) {
        this.MAX_MAIL = maxMail;
        this.SABINE_MAIL = sabineMail;
        this.FREDDY_MAIL = freddyMail;
    }

    @Override
    public void sendMail(String fromPrincipal, SendMailRequest request) {
        log.info("Sending mail from {} to {}", fromPrincipal, request.message().toRecipients());
    }

    @Override
    public OutlookCalendarViewResponse getCalendarView(String userPrincipalName, String start, String end) {
        log.info("Getting calendar view of {}", userPrincipalName);

        ZoneId berlinZone = ZoneId.of("Europe/Berlin");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        // Dynamische Zeiten
        String today9 = LocalDateTime.now(berlinZone).withHour(9).withMinute(0).withSecond(0).format(formatter);
        String today10 = LocalDateTime.now(berlinZone).withHour(10).withMinute(0).withSecond(0).format(formatter);
        String tomorrow14 = LocalDateTime.now(berlinZone).plusDays(1).withHour(14).withMinute(0).withSecond(0).format(formatter);
        String tomorrow1530 = LocalDateTime.now(berlinZone).plusDays(1).withHour(15).withMinute(30).withSecond(0).format(formatter);

        String today11 = LocalDateTime.now(berlinZone).withHour(11).withMinute(0).withSecond(0).format(formatter);
        String today1230 = LocalDateTime.now(berlinZone).withHour(12).withMinute(30).withSecond(0).format(formatter);
        String today16 = LocalDateTime.now(berlinZone).withHour(16).withMinute(0).withSecond(0).format(formatter);
        String today1630 = LocalDateTime.now(berlinZone).withHour(16).withMinute(30).withSecond(0).format(formatter);

        String today13 = LocalDateTime.now(berlinZone).withHour(13).withMinute(0).withSecond(0).format(formatter);
        String today14 = LocalDateTime.now(berlinZone).withHour(14).withMinute(0).withSecond(0).format(formatter);
        String today1730 = LocalDateTime.now(berlinZone).withHour(17).withMinute(30).withSecond(0).format(formatter);
        String today1830 = LocalDateTime.now(berlinZone).withHour(18).withMinute(30).withSecond(0).format(formatter);

        List<OutlookCalendarEvent> events;

        if (userPrincipalName.equals(MAX_MAIL)) {
            events = List.of(
                    new OutlookCalendarEvent(
                            1L,
                            "Team Meeting",
                            new DateTimeTimeZone(today9, "Europe/Berlin"),
                            new DateTimeTimeZone(today10, "Europe/Berlin"),
                            new ItemBody("Text", "Wöchentliches Teammeeting im Büro.")
                    ),
                    new OutlookCalendarEvent(
                            2L,
                            "Kundenpräsentation",
                            new DateTimeTimeZone(tomorrow14, "Europe/Berlin"),
                            new DateTimeTimeZone(tomorrow1530, "Europe/Berlin"),
                            new ItemBody("HTML", "<b>Präsentation</b> beim Kunden X.")
                    )
            );
        } else if (userPrincipalName.equals(SABINE_MAIL)) {
            events = List.of(
                    new OutlookCalendarEvent(
                            3L,
                            "Projekt Kickoff",
                            new DateTimeTimeZone(today11, "Europe/Berlin"),
                            new DateTimeTimeZone(today1230, "Europe/Berlin"),
                            new ItemBody("Text", "Projektstart mit Team und Stakeholdern.")
                    ),
                    new OutlookCalendarEvent(
                            4L,
                            "1:1 Gespräch",
                            new DateTimeTimeZone(today16, "Europe/Berlin"),
                            new DateTimeTimeZone(today1630, "Europe/Berlin"),
                            new ItemBody("Text", "Individuelles Mitarbeitergespräch.")
                    )
            );
        } else if (userPrincipalName.equals(FREDDY_MAIL)) {
            events = List.of(
                    new OutlookCalendarEvent(
                            5L,
                            "Code Review",
                            new DateTimeTimeZone(today13, "Europe/Berlin"),
                            new DateTimeTimeZone(today14, "Europe/Berlin"),
                            new ItemBody("Text", "Review der neuen API-Endpunkte.")
                    ),
                    new OutlookCalendarEvent(
                            6L,
                            "Feierabend-Termin",
                            new DateTimeTimeZone(today1730, "Europe/Berlin"),
                            new DateTimeTimeZone(today1830, "Europe/Berlin"),
                            new ItemBody("Text", "Afterwork mit Kollegen.")
                    )
            );
        } else {
            events = List.of();
        }

        return new OutlookCalendarViewResponse(events);
    }

}

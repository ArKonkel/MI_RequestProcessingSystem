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

    private static final ZoneId BERLIN_ZONE = ZoneId.of("Europe/Berlin");
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private final String PRINCIPAL_ONE_MAIL;
    private final String PRINCIPAL_TWO_MAIL;
    private final String PRINCIPAL_THREE_MAIL;
    private final String PRINCIPAL_FOUR_MAIL;
    private final String PRINCIPAL_FIVE_MAIL;

    public OutlookServiceImpl(
            @Value("${userPrincipalOne}") String principalOne,
            @Value("${userPrincipalTwo}") String principalTwo,
            @Value("${userPrincipalThree}") String principalThree,
            @Value("${userPrincipalFour}") String principalFour,
            @Value("${userPrincipalFive}") String principalFive) {
        this.PRINCIPAL_ONE_MAIL = principalOne.toLowerCase();
        this.PRINCIPAL_TWO_MAIL = principalTwo.toLowerCase();
        this.PRINCIPAL_THREE_MAIL = principalThree.toLowerCase();
        this.PRINCIPAL_FOUR_MAIL = principalFour.toLowerCase();
        this.PRINCIPAL_FIVE_MAIL = principalFive.toLowerCase();
    }

    @Override
    public void sendMail(String fromPrincipal, SendMailRequest request) {
        log.info("Sending mail from {} to {}", fromPrincipal,
                request.message().toRecipients().stream()
                        .map(r -> r.emailAddress().address())
                        .collect(Collectors.joining(", ")));
    }

    @Override
    public OutlookCalendarViewResponse getCalendarView(String userPrincipalName, String start, String end) {
        log.info("Getting calendar view of {}", userPrincipalName);
        userPrincipalName = userPrincipalName.toLowerCase();

        List<OutlookCalendarEvent> events;

        if (userPrincipalName.equals(PRINCIPAL_ONE_MAIL)) {
            events = List.of(
                    new OutlookCalendarEvent("ONE-001", "Team Meeting", time(0, 9, 0), time(0, 10, 0),
                            new ItemBody("Text", "Wöchentliches Teammeeting im Büro.")),
                    new OutlookCalendarEvent("ONE-002", "Kundenpräsentation", time(1, 14, 0), time(1, 15, 30),
                            new ItemBody("HTML", "<b>Präsentation</b> bei Kunde X.")),
                    new OutlookCalendarEvent("ONE-003", "Projekt Sync", time(3, 11, 0), time(3, 12, 0),
                            new ItemBody("Text", "Kurzer Abgleich zum Projektfortschritt.")),
                    new OutlookCalendarEvent("ONE-004", "Wochenplanung", time(5, 9, 30), time(5, 10, 30),
                            new ItemBody("Text", "Planung der nächsten Woche.")),
                    new OutlookCalendarEvent("ONE-005", "All-Hands Meeting", time(6, 16, 0), time(6, 17, 0),
                            new ItemBody("HTML", "Unternehmensweites <b>All-Hands</b> Meeting.")),
                    new OutlookCalendarEvent("ONE-006", "1:1 Feedback", time(7, 10, 30), time(7, 11, 0),
                            new ItemBody("Text", "Feedbackgespräch mit Mitarbeiterin Lisa."))
            );

        } else if (userPrincipalName.equals(PRINCIPAL_TWO_MAIL)) {
            events = List.of(
                    new OutlookCalendarEvent("TWO-001", "Projekt Kickoff", time(0, 10, 0), time(0, 12, 0),
                            new ItemBody("Text", "Projektstart mit Stakeholdern.")),
                    new OutlookCalendarEvent("TWO-002", "Daily Standup", time(2, 9, 0), time(2, 9, 30),
                            new ItemBody("Text", "Tägliches Status-Update.")),
                    new OutlookCalendarEvent("TWO-003", "Serverwartung", time(5, 22, 0), time(6, 2, 0),
                            new ItemBody("Text", "Overnight Maintenance.")),
                    new OutlookCalendarEvent("TWO-004", "Budget Review", time(3, 14, 0), time(3, 15, 0),
                            new ItemBody("HTML", "Überprüfung der <i>Projektbudgets</i>.")),
                    new OutlookCalendarEvent("TWO-005", "Kaffee mit HR", time(4, 10, 0), time(4, 10, 45),
                            new ItemBody("Text", "Austausch mit HR-Team.")),
                    new OutlookCalendarEvent("TWO-006", "Code-Freeze Review", time(7, 15, 0), time(7, 16, 0),
                            new ItemBody("Text", "Review der Release-Branch vor Freeze."))
            );

        } else if (userPrincipalName.equals(PRINCIPAL_THREE_MAIL)) {
            events = List.of(
                    new OutlookCalendarEvent("THREE-001", "Code Review", time(0, 13, 0), time(0, 14, 0),
                            new ItemBody("Text", "Review neuer API-Endpunkte.")),
                    new OutlookCalendarEvent("THREE-002", "Team Lunch", time(2, 12, 0), time(2, 13, 0),
                            new ItemBody("Text", "Gemeinsames Mittagessen.")),
                    new OutlookCalendarEvent("THREE-003", "Afterwork Meetup", time(4, 18, 0), time(4, 19, 0),
                            new ItemBody("Text", "Feierabend mit Kollegen.")),
                    new OutlookCalendarEvent("THREE-004", "Pair Programming", time(3, 10, 0), time(3, 11, 30),
                            new ItemBody("Text", "Zusammen mit Anna am neuen Feature arbeiten.")),
                    new OutlookCalendarEvent("THREE-005", "Refactoring Sprint", time(5, 14, 0), time(5, 16, 0),
                            new ItemBody("Text", "Aufräumen alter Klassen im Modul 'Reports'.")),
                    new OutlookCalendarEvent("THREE-006", "Customer Feedback Call", time(6, 11, 0), time(6, 11, 30),
                            new ItemBody("HTML", "Feedbackrunde mit <b>Kunde ACME Corp</b>."))
            );

        } else if (userPrincipalName.equals(PRINCIPAL_FOUR_MAIL)) {
            events = List.of(
                    new OutlookCalendarEvent("FOUR-001", "Strategiemeeting", time(2, 10, 0), time(2, 11, 0),
                            new ItemBody("Text", "Besprechung zur Produktstrategie.")),
                    new OutlookCalendarEvent("FOUR-002", "Workshop Datenanalyse", time(4, 9, 0), time(4, 12, 0),
                            new ItemBody("HTML", "<i>Intensivtraining</i> mit Data-Science-Team.")),
                    new OutlookCalendarEvent("FOUR-003", "Sprint Planning", time(6, 13, 0), time(6, 14, 0),
                            new ItemBody("Text", "Planung des kommenden Sprints.")),
                    new OutlookCalendarEvent("FOUR-004", "Team Retro", time(1, 16, 0), time(1, 17, 0),
                            new ItemBody("Text", "Sprint Retrospektive.")),
                    new OutlookCalendarEvent("FOUR-005", "Board Meeting", time(3, 15, 0), time(3, 17, 0),
                            new ItemBody("HTML", "Vorstellung der Quartalsergebnisse.")),
                    new OutlookCalendarEvent("FOUR-006", "Mentoring-Session", time(7, 9, 0), time(7, 10, 0),
                            new ItemBody("Text", "Coaching-Gespräch mit Nachwuchskraft."))
            );

        } else if (userPrincipalName.equals(PRINCIPAL_FIVE_MAIL)) {
            events = List.of(
                    new OutlookCalendarEvent("FIVE-001", "Design Review", time(1, 11, 0), time(1, 12, 0),
                            new ItemBody("Text", "Review neuer UI-Komponenten.")),
                    new OutlookCalendarEvent("FIVE-002", "Kundentermin (remote)", time(3, 15, 0), time(3, 16, 30),
                            new ItemBody("HTML", "Online-Termin via Teams mit Kunde <b>Y</b>.")),
                    new OutlookCalendarEvent("FIVE-003", "Wochenrückblick", time(7, 17, 0), time(7, 18, 0),
                            new ItemBody("Text", "Review der Woche & Planung der nächsten.")),
                    new OutlookCalendarEvent("FIVE-004", "UX Workshop", time(2, 9, 30), time(2, 11, 30),
                            new ItemBody("HTML", "Workshop zu <i>User Flows</i> im neuen Portal.")),
                    new OutlookCalendarEvent("FIVE-005", "Design Sync", time(4, 14, 0), time(4, 15, 0),
                            new ItemBody("Text", "Abgleich Design & Entwicklung.")),
                    new OutlookCalendarEvent("FIVE-006", "Produktdemo", time(6, 10, 0), time(6, 11, 0),
                            new ItemBody("HTML", "Interne Demo des neuen Features für das Sales-Team."))
            );

        } else {
            events = List.of();
        }

        return new OutlookCalendarViewResponse(events);
    }

    /** Utility-Methode zur einfachen Erstellung von Datum/Zeit-Objekten */
    private DateTimeTimeZone time(int daysFromNow, int hour, int minute) {
        String dateTime = LocalDateTime.now(BERLIN_ZONE)
                .plusDays(daysFromNow)
                .withHour(hour)
                .withMinute(minute)
                .withSecond(0)
                .format(FMT);
        return new DateTimeTimeZone(dateTime, "Europe/Berlin");
    }
}

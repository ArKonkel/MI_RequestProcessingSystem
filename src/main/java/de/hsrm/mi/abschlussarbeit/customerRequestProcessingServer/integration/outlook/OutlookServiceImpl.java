package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.integration.outlook;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.calendar.CalendarService;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.customerRequest.CustomerRequest;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.integration.outlook.graphTypes.OutlookCalendarViewResponse;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.mail.EmailAddress;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.mail.MailService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class OutlookServiceImpl implements MailService, OutlookCalendarService {

    private final CalendarService calendarService;

    @Override
    public void sendMails(CustomerRequest request, List<EmailAddress> recipients) {
        System.out.println("Sending mail to " + recipients.size() + " recipients");

        //TODO implement me
    }

    @Override
    public void initCalendarOfEmployee(Long employeeId) {

    }

    @Override
    public OutlookCalendarViewResponse fetchCalendarEvents(String employeeMail, OffsetDateTime start, OffsetDateTime end) {
        return null;
    }
}

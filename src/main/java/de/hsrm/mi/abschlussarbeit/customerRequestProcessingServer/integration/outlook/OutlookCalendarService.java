package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.integration.outlook;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.integration.outlook.graphTypes.OutlookCalendarViewResponse;

import java.time.OffsetDateTime;

public interface OutlookCalendarService {

    OutlookCalendarViewResponse fetchCalendarEvents(String employeeMail, OffsetDateTime start, OffsetDateTime end);
}

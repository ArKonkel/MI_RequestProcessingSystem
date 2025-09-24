package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.integration.outlook.graphTypes;

import java.util.List;

//TODO how to map calendar to employee?
public record OutlookCalendarViewResponse(List<OutlookCalendarEvent> value) {
}

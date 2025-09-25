package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.integration.outlook.graphTypes;

import java.util.List;

public record OutlookCalendarViewResponse(List<OutlookCalendarEvent> value) {
}

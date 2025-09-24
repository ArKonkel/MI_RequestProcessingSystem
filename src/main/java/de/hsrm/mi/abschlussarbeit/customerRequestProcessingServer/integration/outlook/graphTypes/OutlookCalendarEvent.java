package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.integration.outlook.graphTypes;

public record OutlookCalendarEvent(Long id, String subject, DateTimeTimeZone start, DateTimeTimeZone end,
                                   ItemBody body) {
}

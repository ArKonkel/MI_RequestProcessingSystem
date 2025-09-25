package de.hsrm.mi.outlookdummy.microsoftGraphAPI.types;

public record OutlookCalendarEvent(String id, String subject, DateTimeTimeZone start, DateTimeTimeZone end,
                                   ItemBody body) {
}

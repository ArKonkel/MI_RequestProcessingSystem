package de.hsrm.mi.outlookdummy.microsoftGraphAPI.types;

public record OutlookCalendarEvent(Long id, String subject, DateTimeTimeZone start, DateTimeTimeZone end,
                                   ItemBody body) {
}

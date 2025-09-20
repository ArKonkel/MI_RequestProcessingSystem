package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.calendar;


public record IncomingCalendarEntryDto(String id) {
}
/*
//TODO create IncomingCalendarEntryDto and Mapper to CalenderEntryDto
public record IncomingCalendarEntryDto(
        String id,
        String subject,
        BodyDto body,
        DateTimeWrapper start,
        DateTimeWrapper end,
        boolean isAllDay,
        String showAs,
        List<String> categories,
        LocationDto location,
        OrganizerDto organizer
) {}

public record BodyDto(String contentType, String content) {}

public record DateTimeWrapper(String dateTime, String timeZone) {}

public record LocationDto(String displayName) {}

public record OrganizerDto(EmailAddressDto emailAddress) {}

public record EmailAddressDto(String name, String address) {}

 */
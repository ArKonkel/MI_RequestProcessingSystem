package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.calendar;


import org.mapstruct.Mapper;

/**
 * Mapper class for {@link CalendarEntry} and {@link CalendarEntryDto}
 */
@Mapper(componentModel = "spring")
public interface CalendarEntryMapper {

    CalendarEntryDto toDto(CalendarEntry calendarEntry);

}

package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.calendar;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper class for {@link CalendarEntry} and {@link CalendarEntryDto}
 */
@Mapper(componentModel = "spring")
public interface CalendarEntryMapper {

    @Mapping(target = "taskId", source = "task.id")
    CalendarEntryDto toDto(CalendarEntry calendarEntry);

}

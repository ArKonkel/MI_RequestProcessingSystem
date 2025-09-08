package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.calendarModule.mapper;


import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.calendarModule.dto.CalendarEntryDto;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.calendarModule.entity.CalendarEntry;
import org.mapstruct.Mapper;

/**
 * Mapper class for {@link CalendarEntry} and {@link CalendarEntryDto}
 */
@Mapper(componentModel = "spring")
public interface CalendarEntryMapper {

    CalendarEntryDto toDto(CalendarEntry calendarEntry);

}

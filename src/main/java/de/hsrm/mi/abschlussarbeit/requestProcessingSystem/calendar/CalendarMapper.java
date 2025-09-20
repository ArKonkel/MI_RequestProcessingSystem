package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.calendar;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


/**
 * Mapper class for {@link Calendar} and {@link CalendarDto}
 */
@Mapper(componentModel = "spring", uses = {CalendarEntryMapper.class})
public interface CalendarMapper {

    @Mapping(source = "employee.id", target = "employeeId")
    CalendarDto toDto(Calendar calendar);
}

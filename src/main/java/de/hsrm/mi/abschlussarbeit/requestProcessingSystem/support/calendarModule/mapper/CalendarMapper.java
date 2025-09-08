package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.calendarModule.mapper;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.calendarModule.dto.CalendarDto;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.calendarModule.entity.Calendar;
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

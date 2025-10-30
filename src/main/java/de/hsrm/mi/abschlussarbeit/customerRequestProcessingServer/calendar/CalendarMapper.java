package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.calendar;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


/**
 * Mapper class for {@link Calendar} and {@link CalendarDto}
 */
@Mapper(componentModel = "spring", uses = {CalendarEntryMapper.class})
public interface CalendarMapper {

    @Mapping(source = "owner.workingHoursPerDay", target = "ownerWorkingHoursPerDay")
    @Mapping(source = "owner.firstName", target = "ownerFirstName")
    @Mapping(source = "owner.lastName", target = "ownerLastName")
    @Mapping(source = "owner.id", target = "ownerId")
    CalendarDto toDto(Calendar calendar);
}

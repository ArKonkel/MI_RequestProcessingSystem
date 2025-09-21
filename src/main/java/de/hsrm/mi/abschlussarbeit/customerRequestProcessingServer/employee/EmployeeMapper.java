package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.employee;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.competence.CompetenceMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper class for {@link Employee} and {@link EmployeeDto}
 */
@Mapper(componentModel = "spring", uses = {EmployeeExpertiseMapper.class, CompetenceMapper.class})
public interface EmployeeMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "calendar.id", target = "calendarId")
    @Mapping(source = "department.id", target = "departmentId")
    EmployeeDto toDto(Employee employee);

    @Mapping(target = "department", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "calendar", ignore = true)
    Employee toEntity(EmployeeDto dto);
}
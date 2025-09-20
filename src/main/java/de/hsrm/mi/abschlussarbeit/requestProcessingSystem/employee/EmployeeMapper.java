package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.employee;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.competence.CompetenceMapper;
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
}
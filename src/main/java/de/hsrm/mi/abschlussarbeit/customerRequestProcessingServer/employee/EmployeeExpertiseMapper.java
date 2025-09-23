package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.employee;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.expertise.ExpertiseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


/**
 * Mapper class for {@link EmployeeExpertise} and {@link EmployeeExpertiseDto}
 */
@Mapper(componentModel = "spring", uses = ExpertiseMapper.class)
public interface EmployeeExpertiseMapper {

    @Mapping(source = "employee.id", target = "employeeId")
    EmployeeExpertiseDto toDto(EmployeeExpertise employeeExpertise);

    @Mapping(target = "employee", ignore = true)
    @Mapping(target = "expertise.tasks", ignore = true)
    EmployeeExpertise toEntity(EmployeeExpertiseDto dto);

}

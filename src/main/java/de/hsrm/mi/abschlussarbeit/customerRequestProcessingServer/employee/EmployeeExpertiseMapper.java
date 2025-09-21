package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.employee;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.competence.CompetenceMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


/**
 * Mapper class for {@link EmployeeExpertise} and {@link EmployeeExpertiseDto}
 */
@Mapper(componentModel = "spring", uses = CompetenceMapper.class)
public interface EmployeeExpertiseMapper {

    @Mapping(source = "employee.id", target = "employeeId")
    EmployeeExpertiseDto toDto(EmployeeExpertise employeeExpertise);

    @Mapping(target = "employee", ignore = true)
    EmployeeExpertise toEntity(EmployeeExpertiseDto dto);

}

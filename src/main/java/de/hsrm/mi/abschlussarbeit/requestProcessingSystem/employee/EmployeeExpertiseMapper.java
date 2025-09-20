package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.employee;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.competence.CompetenceMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


/**
 * Mapper class for {@link EmployeeExpertise} and {@link EmployeeExpertiseDto}
 */
@Mapper(componentModel = "spring", uses = CompetenceMapper.class)
public interface EmployeeExpertiseMapper {

    @Mapping(source = "employee.id", target = "employeeId")
    EmployeeExpertiseDto toDto(EmployeeExpertise employeeExpertise);
}

package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.employee;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.competence.CompetenceDto;

public record EmployeeExpertiseDto (
        long id,
        long employeeId,
        CompetenceDto expertise,
        ExpertiseLevel level
) {
}

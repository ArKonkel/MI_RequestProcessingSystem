package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.employee;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.competence.CompetenceDto;

public record EmployeeExpertiseDto (
        long id,
        long employeeId,
        CompetenceDto expertise,
        ExpertiseLevel level
) {
}

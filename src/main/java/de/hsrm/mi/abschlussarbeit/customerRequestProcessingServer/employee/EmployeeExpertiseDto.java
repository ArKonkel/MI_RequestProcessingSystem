package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.employee;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.expertise.ExpertiseDto;

public record EmployeeExpertiseDto (
        long id,
        long employeeId,
        ExpertiseDto expertise,
        ExpertiseLevel level
) {
}

package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.userManager.dto;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.enums.ExpertiseLevel;

public record EmployeeExpertiseDto (
        long id,
        long employeeId,
        CompetenceDto expertise,
        ExpertiseLevel level
) {
}

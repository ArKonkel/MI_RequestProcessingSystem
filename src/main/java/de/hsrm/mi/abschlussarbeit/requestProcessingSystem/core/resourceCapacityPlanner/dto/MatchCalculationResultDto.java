package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.resourceCapacityPlanner.dto;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.userManager.dto.EmployeeDto;

import java.util.List;

public record MatchCalculationResultDto(
        EmployeeDto employee,
        Long competencePoints,
        Boolean canCompleteTaskEarliest,
        List<CalculatedCapacityCalendarEntryDto> calculatedCalendarCapacities
) {}

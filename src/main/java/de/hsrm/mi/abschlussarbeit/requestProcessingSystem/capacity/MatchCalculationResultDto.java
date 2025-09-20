package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.capacity;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.employee.EmployeeDto;

import java.util.List;

public record MatchCalculationResultDto(
        EmployeeDto employee,
        Long competencePoints,
        Boolean canCompleteTaskEarliest,
        List<CalculatedCapacityCalendarEntryDto> calculatedCalendarCapacities
) {}

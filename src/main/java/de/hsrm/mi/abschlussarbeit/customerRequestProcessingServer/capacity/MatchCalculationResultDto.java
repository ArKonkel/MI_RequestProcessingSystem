package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.capacity;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.employee.EmployeeDto;

import java.util.List;

public record MatchCalculationResultDto(
        EmployeeDto employee,
        Long competencePoints,
        Boolean canCompleteTaskEarliest,
        List<CalculatedCapacityCalendarEntryDto> calculatedCalendarCapacities
) {}

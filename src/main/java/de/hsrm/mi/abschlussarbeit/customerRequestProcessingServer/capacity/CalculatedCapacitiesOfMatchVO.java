package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.capacity;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.employee.Employee;

import java.util.List;

public record CalculatedCapacitiesOfMatchVO(
        Employee employee,
        Long expertisePoints,
        Boolean canCompleteTaskEarliest,
        List<CalculatedCapacityCalendarEntryVO> calculatedCalendarCapacities
) {}

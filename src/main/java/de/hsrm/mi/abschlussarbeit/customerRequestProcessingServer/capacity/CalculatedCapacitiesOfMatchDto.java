package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.capacity;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.employee.EmployeeDto;
import lombok.Data;

import java.util.List;

@Data
public class CalculatedCapacitiesOfMatchDto {
    private EmployeeDto employee;
    private Long competencePoints;
    private Boolean canCompleteTaskEarliest;
    private List<CalculatedCapacityCalendarEntryDto> calculatedCalendarCapacities;
}
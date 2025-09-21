package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.capacity;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.employee.Employee;
import lombok.Data;

import java.util.List;

@Data
public class CalculatedCapacitiesOfMatchDto {
    private Employee employee; //TODO should be EmployeeDto
    private Long competencePoints;
    private Boolean canCompleteTaskEarliest;
    private List<CalculatedCapacityCalendarEntryDto> calculatedCalendarCapacities;
}
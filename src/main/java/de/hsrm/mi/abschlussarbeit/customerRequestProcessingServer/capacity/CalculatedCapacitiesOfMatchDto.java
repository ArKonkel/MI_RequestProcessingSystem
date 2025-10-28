package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.capacity;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.employee.EmployeeDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CalculatedCapacitiesOfMatchDto {
    private EmployeeDto employee;
    private Long expertisePoints;
    private Boolean canCompleteTaskEarliest;
    private List<CalculatedCapacityCalendarEntryDto> calculatedCalendarCapacities;
}
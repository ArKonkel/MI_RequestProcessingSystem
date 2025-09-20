package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.capacity;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.task.TaskDto;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.employee.EmployeeDto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface CapacityCalculatorEngine {

    List<CalculatedCapacityCalendarEntryDto> calculateFreeCapacity(TaskDto taskDto, Long employeeId, LocalDate from, LocalDate to);

    List<EmployeeDto> calculateEmployeesAbleToCompleteTaskEarliest(Map<EmployeeDto, List<CalculatedCapacityCalendarEntryDto>> employeeWithCalendarEntriesOfTask);

}

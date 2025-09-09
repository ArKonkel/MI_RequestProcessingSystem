package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.resourceCapacityPlanner.service;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.taskManager.dto.TaskDto;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.calendarModule.dto.CalendarEntryDto;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.userManager.dto.EmployeeDto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface CapacityCalculatorEngine {

    List<CalculatedCalendarEntryDto> calculateFreeCapacity(TaskDto taskDto, Long employeeId, LocalDate from, LocalDate to);

    List<EmployeeDto> calculateEmployeesAbleToCompleteTaskEarliest(Map<EmployeeDto, List<CalendarEntryDto>> employeeWithCalendarEntriesOfTask);

}

package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.capacity;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.employee.Employee;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.task.Task;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface CapacityCalculator {

    List<CalculatedCapacityCalendarEntryVO> calculateFreeCapacity(Task task, Long employeeId, LocalDate from, LocalDate to);

    List<Employee> calculateEmployeesAbleToCompleteTaskEarliest(Map<Employee, List<CalculatedCapacityCalendarEntryVO>> employeeWithCalendarEntriesOfTask);

}

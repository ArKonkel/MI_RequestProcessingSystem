package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.capacity;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.task.TaskDto;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.employee.EmployeeDto;

import java.util.Map;

public interface TaskMatcher {

    Map<EmployeeDto, Integer> findBestMatchingEmployees(TaskDto task);
}

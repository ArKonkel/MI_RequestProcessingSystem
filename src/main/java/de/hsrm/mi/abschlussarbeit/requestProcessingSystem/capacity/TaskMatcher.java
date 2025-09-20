package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.capacity;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.task.TaskDto;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.employee.EmployeeDto;

import java.util.Map;

public interface TaskMatcher {

    Map<EmployeeDto, Integer> findBestMatchingEmployees(TaskDto task);
}

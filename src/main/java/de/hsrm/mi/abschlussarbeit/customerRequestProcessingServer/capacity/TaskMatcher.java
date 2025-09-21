package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.capacity;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.employee.Employee;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.task.Task;

import java.util.Map;

public interface TaskMatcher {

    Map<Employee, Integer> findBestMatchingEmployees(Task task);
}

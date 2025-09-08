package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.resourceCapacityPlanner.service;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.taskManager.dto.TaskDto;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.userManager.dto.EmployeeDto;

import java.util.Map;

public interface ResourceCapacityService {

    Map<EmployeeDto, Integer> findBestMatchingEmployeesWithCalendar(TaskDto task);

}

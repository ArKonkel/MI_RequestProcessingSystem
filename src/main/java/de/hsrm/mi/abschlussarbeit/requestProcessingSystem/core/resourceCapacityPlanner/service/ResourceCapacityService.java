package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.resourceCapacityPlanner.service;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.resourceCapacityPlanner.dto.MatchingEmployeeForTaskDto;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.taskManager.dto.TaskDto;

public interface ResourceCapacityService {

    MatchingEmployeeForTaskDto findBestMatches(TaskDto task);

}

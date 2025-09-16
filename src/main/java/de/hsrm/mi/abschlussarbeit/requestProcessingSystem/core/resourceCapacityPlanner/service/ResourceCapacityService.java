package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.resourceCapacityPlanner.service;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.resourceCapacityPlanner.dto.MatchCalculationResultDto;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.resourceCapacityPlanner.dto.MatchingEmployeeForTaskDto;

public interface ResourceCapacityService {

    MatchingEmployeeForTaskDto findBestMatches(Long taskId);

    void assignTaskToEmployee(Long taskId, MatchCalculationResultDto selectedMatch);

}

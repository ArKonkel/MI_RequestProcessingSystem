package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.resourceCapacityPlanner.dto;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.taskManager.dto.TaskDto;

import java.util.List;

public record MatchingEmployeeForTaskDto(
        TaskDto task,
        List<MatchCalculationResultDto> matchCalculationResult) {
}

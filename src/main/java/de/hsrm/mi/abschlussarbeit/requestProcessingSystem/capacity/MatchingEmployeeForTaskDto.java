package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.capacity;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.task.TaskDto;

import java.util.List;

public record MatchingEmployeeForTaskDto(
        TaskDto task,
        List<MatchCalculationResultDto> matchCalculationResult) {
}

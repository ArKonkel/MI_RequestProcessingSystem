package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.capacity;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.task.TaskDto;

import java.util.List;

public record MatchingEmployeeForTaskDto(
        TaskDto task,
        List<MatchCalculationResultDto> matchCalculationResult) {
}

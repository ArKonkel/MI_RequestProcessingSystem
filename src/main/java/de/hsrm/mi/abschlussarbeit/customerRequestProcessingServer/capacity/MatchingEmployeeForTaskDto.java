package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.capacity;

import java.util.List;

public record MatchingEmployeeForTaskDto(
        Long taskId,
        List<MatchCalculationResultVO> matchCalculationResult) {
}

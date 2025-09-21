package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.capacity;

import java.util.List;

public record MatchingEmployeeForTaskVO(
        Long taskId,
        List<MatchCalculationResultVO> matchCalculationResult) {
}

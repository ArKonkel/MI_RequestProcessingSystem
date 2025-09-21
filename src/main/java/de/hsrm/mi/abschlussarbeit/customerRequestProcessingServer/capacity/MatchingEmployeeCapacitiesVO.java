package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.capacity;

import java.util.List;

public record MatchingEmployeeCapacitiesVO(
        Long taskId,
        List<CalculatedCapacitiesOfMatchVO> matchCalculationResult) {
}

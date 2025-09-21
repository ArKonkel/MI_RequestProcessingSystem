package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.capacity;

import lombok.Data;

import java.util.List;

@Data
public class MatchingEmployeeCapacitiesDto {
    private Long taskId;
    private List<CalculatedCapacitiesOfMatchDto> matchCalculationResult;
}
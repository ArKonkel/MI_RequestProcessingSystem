package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.capacity;

public interface ResourceCapacityService {

    MatchingEmployeeForTaskDto findBestMatches(Long taskId);

    void assignTaskToEmployee(Long taskId, MatchCalculationResultDto selectedMatch);

}

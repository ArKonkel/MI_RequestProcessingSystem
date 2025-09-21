package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.capacity;

public interface CapacityService {

    MatchingEmployeeForTaskDto findBestMatches(Long taskId);

    void assignMatchToEmployee(Long taskId, MatchCalculationResultDto selectedMatch);

}

package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.capacity;

public interface CapacityService {

    MatchingEmployeeForTaskVO findBestMatches(Long taskId);

    void assignMatchToEmployee(Long taskId, MatchCalculationResultVO selectedMatch);

}

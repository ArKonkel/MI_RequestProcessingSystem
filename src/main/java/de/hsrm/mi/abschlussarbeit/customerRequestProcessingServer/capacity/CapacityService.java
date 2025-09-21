package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.capacity;

public interface CapacityService {

    MatchingEmployeeCapacitiesVO findBestMatches(Long taskId);

    MatchingEmployeeCapacitiesDto findBestMatchesForTask(Long taskId);

    void assignMatchToEmployee(Long taskId, CalculatedCapacitiesOfMatchDto selectedMatch);

}

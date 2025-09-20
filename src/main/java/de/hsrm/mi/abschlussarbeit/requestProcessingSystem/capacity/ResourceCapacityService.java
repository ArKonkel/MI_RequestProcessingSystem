package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.capacity;

public interface ResourceCapacityService {

    MatchingEmployeeForTaskDto findBestMatches(Long taskId);

    void assignTaskToEmployee(Long taskId, MatchCalculationResultDto selectedMatch);

}

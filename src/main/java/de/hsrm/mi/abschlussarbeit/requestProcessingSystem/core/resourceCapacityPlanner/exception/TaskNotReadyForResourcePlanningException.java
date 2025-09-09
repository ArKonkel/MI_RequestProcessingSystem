package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.resourceCapacityPlanner.exception;

public class TaskNotReadyForResourcePlanningException extends RuntimeException {
    public TaskNotReadyForResourcePlanningException(String message) {
        super(message);
    }
}

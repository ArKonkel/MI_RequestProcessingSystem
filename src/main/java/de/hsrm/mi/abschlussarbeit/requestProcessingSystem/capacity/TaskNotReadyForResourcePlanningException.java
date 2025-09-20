package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.capacity;

public class TaskNotReadyForResourcePlanningException extends RuntimeException {
    public TaskNotReadyForResourcePlanningException(String message) {
        super(message);
    }
}

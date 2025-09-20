package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.capacity;

public class TaskNotReadyForResourcePlanningException extends RuntimeException {
    public TaskNotReadyForResourcePlanningException(String message) {
        super(message);
    }
}

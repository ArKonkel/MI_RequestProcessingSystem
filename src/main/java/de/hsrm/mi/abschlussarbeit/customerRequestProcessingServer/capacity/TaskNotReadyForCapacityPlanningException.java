package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.capacity;

public class TaskNotReadyForCapacityPlanningException extends RuntimeException {
    public TaskNotReadyForCapacityPlanningException(String message) {
        super(message);
    }
}

package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.capacity;

public class TaskAlreadyPlannedException extends RuntimeException {
    public TaskAlreadyPlannedException(String message) {
        super(message);
    }
}

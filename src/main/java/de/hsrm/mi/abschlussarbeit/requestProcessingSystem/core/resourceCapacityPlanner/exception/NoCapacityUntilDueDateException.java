package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.resourceCapacityPlanner.exception;

public class NoCapacityUntilDueDateException extends RuntimeException {
    public NoCapacityUntilDueDateException(String message) {
        super(message);
    }
}

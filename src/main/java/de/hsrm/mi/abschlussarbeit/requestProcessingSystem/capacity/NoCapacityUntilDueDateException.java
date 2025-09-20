package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.capacity;

public class NoCapacityUntilDueDateException extends RuntimeException {
    public NoCapacityUntilDueDateException(String message) {
        super(message);
    }
}

package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.capacity;

public class NoCapacityUntilDueDateException extends RuntimeException {
    public NoCapacityUntilDueDateException(String message) {
        super(message);
    }
}

package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.globalExceptionHandler;

public class NotAllowedException extends RuntimeException {
    public NotAllowedException(String message) {
        super(message);
    }
}
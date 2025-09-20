package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.globalExceptionHandler;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}

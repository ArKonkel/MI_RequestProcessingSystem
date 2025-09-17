package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.globalExceptionHandler;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}

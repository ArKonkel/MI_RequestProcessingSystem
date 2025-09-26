package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.globalExceptionHandler;

public class SaveException extends RuntimeException {
    public SaveException(String message) {
        super(message);
    }
}

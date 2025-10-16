package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.security;

public class CustomerNotInSystemException extends RuntimeException {
    public CustomerNotInSystemException(String message) {
        super(message);
    }
}

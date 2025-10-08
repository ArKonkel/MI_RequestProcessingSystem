package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.user;

public class UserAlreadyExistsException extends RuntimeException {
  public UserAlreadyExistsException(String message) {
    super(message);
  }
}

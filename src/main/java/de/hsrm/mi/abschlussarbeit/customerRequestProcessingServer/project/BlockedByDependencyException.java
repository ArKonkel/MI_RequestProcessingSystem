package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.project;

public class BlockedByDependencyException extends RuntimeException {
  public BlockedByDependencyException(String message) {
    super(message);
  }
}

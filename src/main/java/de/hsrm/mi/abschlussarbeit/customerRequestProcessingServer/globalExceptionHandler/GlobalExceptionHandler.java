package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.globalExceptionHandler;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.project.BlockedByDependencyException;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.project.InvalidDependencyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFound(NotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(SaveException.class)
    public ResponseEntity<String> handleSave(SaveException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }

    @ExceptionHandler(NotAllowedException.class)
    public ResponseEntity<String> handleProjectNotReady(NotAllowedException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    @ExceptionHandler(BlockedByDependencyException.class)
    public ResponseEntity<String> handleBlockedByDependency(BlockedByDependencyException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    @ExceptionHandler(InvalidDependencyException.class)
    public ResponseEntity<String> handleInvalidDependency(InvalidDependencyException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    }

}

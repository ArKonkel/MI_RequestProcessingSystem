package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.globalExceptionHandler;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.capacity.NoCapacityUntilDueDateException;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.capacity.TaskAlreadyPlannedException;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.capacity.TaskNotReadyForCapacityPlanningException;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.project.BlockedByDependencyException;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.project.InvalidDependencyException;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.security.CustomerNotInSystemException;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.user.UserAlreadyExistsException;
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

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<String> handleUserAlreadyExists(UserAlreadyExistsException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(NoCapacityUntilDueDateException.class)
    public ResponseEntity<String> handleNoCapacityUntilDueDate(NoCapacityUntilDueDateException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    @ExceptionHandler(TaskNotReadyForCapacityPlanningException.class)
    public ResponseEntity<String> handleTaskNotReadyForCapacityPlanning(TaskNotReadyForCapacityPlanningException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    @ExceptionHandler(CustomerNotInSystemException.class)
    public ResponseEntity<String> handleCustomerNotInSystem(CustomerNotInSystemException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(TaskAlreadyPlannedException.class)
    public ResponseEntity<String> handleTaskAlreadyPlanned(TaskAlreadyPlannedException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }
}

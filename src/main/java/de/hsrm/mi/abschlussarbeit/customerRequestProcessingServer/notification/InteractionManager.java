package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.notification;

public interface InteractionManager {
    void assignTaskToUserOfEmployee(Long taskId, Long employeeId) ;
}

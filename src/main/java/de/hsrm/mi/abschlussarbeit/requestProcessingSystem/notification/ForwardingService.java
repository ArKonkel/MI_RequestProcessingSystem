package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.notification;

public interface ForwardingService {

    void assignTaskToUserOfEmployee(Long taskId, Long employeeId);
}

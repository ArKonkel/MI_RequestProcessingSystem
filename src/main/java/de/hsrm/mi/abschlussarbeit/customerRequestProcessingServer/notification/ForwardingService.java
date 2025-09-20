package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.notification;

public interface ForwardingService {

    void assignTaskToUserOfEmployee(Long taskId, Long employeeId);
}

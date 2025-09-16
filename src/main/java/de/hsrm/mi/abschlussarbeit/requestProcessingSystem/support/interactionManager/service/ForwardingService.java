package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.interactionManager.service;

public interface ForwardingService {

    void assignTaskToUserOfEmployee(Long taskId, Long employeeId);
}

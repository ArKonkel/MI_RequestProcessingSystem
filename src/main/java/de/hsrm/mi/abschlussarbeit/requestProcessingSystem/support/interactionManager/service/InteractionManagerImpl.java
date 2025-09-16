package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.interactionManager.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class InteractionManagerImpl implements InteractionManager {

    private final ForwardingService forwardingService;

    @Override
    public void assignTaskToUserOfEmployee(Long taskId, Long employeeId) {

        forwardingService.assignTaskToUserOfEmployee(taskId, employeeId);
    }
}

package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.processItem;

public interface ProcessItemService {

    void assignProcessItemToUserOfEmployee(Long processItemId, Long employeeId);

    ProcessItem getProcessItemById(Long id);
}

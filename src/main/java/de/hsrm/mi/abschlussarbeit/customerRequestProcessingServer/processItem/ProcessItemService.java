package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.processItem;

public interface ProcessItemService {

    void assignProcessItemToUser(Long processItemId, Long userId);

    ProcessItem getProcessItemById(Long id);
}

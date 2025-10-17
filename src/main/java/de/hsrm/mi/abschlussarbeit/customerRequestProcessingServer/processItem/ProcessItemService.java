package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.processItem;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ProcessItemService {

    void assignProcessItemToUser(Long processItemId, Long userId);

    ProcessItem getProcessItemById(Long id);

    void addAttachment(Long processItemId, MultipartFile file) throws IOException;
}

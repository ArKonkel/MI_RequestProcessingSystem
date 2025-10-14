package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.processItem;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.file.FileDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ProcessItemService {

    void assignProcessItemToUser(Long processItemId, Long userId);

    ProcessItem getProcessItemById(Long id);

    FileDto addAttachment(Long processItemId, MultipartFile file) throws IOException;
}

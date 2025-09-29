package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.processItem;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.globalExceptionHandler.NotFoundException;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.user.User;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.user.UserService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class ProcessItemImpl implements ProcessItemService {

    private final ProcessItemRepository processItemRepository;

    private final UserService userService;

    @Override
    @Transactional
    public void assignProcessItemToUserOfEmployee(Long processItemId, Long employeeId) {
        log.info("Assigning process item {} to employee {}", processItemId, employeeId);

        ProcessItem processItem = processItemRepository.getReferenceById(processItemId);
        User user = userService.getUserOfEmployee(employeeId);

        processItem.setAssignee(user);

        processItemRepository.save(processItem);
    }

    @Override
    public ProcessItem getProcessItemById(Long id) {

        return processItemRepository.findById(id).orElseThrow(() -> new NotFoundException("Process item with id " + id + " not found"));
    }
}

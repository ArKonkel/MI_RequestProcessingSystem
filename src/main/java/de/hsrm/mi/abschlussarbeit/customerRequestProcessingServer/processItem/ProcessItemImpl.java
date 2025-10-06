package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.processItem;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.customerRequest.CustomerRequest;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.globalExceptionHandler.NotFoundException;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.notification.*;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.project.Project;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.task.Task;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.user.User;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.user.UserService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class ProcessItemImpl implements ProcessItemService {

    private final NotificationService notificationService;

    private final ProcessItemRepository processItemRepository;

    private final UserService userService;

    @Override
    @Transactional
    public void assignProcessItemToUser(Long processItemId, Long userId) {
        log.info("Assigning process item {} to user {}", processItemId, userId);

        ProcessItem processItem = getProcessItemById(processItemId);
        User user = userService.getUserById(userId);

        processItem.setAssignee(user);

        processItemRepository.save(processItem);

        TargetType targetType = null;
        switch (processItem) {
            case Task task -> targetType = TargetType.TASK;
            case CustomerRequest customerRequest -> targetType = TargetType.CUSTOMER_REQUEST;
            case Project project -> targetType = TargetType.PROJECT;
            default -> {
            }
        }

        notificationService.sendChangeNotification(new ChangeNotificationEvent(processItem.getId(), ChangeType.UPDATED, targetType));

        notificationService.sendUserNotification(new UserNotificationEvent(UserNotificationType.ASSIGNED, processItem.getId(), processItem.getTitle(),
                List.of(user.getId()), "", Instant.now()));
    }

    @Override
    public ProcessItem getProcessItemById(Long id) {

        return processItemRepository.findById(id).orElseThrow(() -> new NotFoundException("Process item with id " + id + " not found"));
    }
}

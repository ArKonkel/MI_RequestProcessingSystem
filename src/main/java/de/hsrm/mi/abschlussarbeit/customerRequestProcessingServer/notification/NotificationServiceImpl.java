package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.notification;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void sendUserNotification(UserNotificationEvent event) {
        log.info("Sending notification event {}", event);

        if (event.type() == UserNotificationType.ASSIGNED) {
            messagingTemplate.convertAndSend(Topic.PROCESS_ITEM_ASSIGNED.getPath(), event);

        } else if (event.type() == UserNotificationType.COMMENT_MENTIONING) {
            messagingTemplate.convertAndSend(Topic.IN_COMMENT_MENTIONED.getPath(), event);
        }
    }

    @Override
    public void sendChangeNotification(ChangeNotificationEvent event) {
        log.info("Sending change message event {}", event);

        if (event.targetType() == TargetType.CUSTOMER_REQUEST) {
            messagingTemplate.convertAndSend(Topic.CUSTOMER_REQUEST_CHANGED.getPath(), event);

        } else if (event.targetType() == TargetType.TASK) {
            messagingTemplate.convertAndSend(Topic.TASK_CHANGED.getPath(), event);

        } else if (event.targetType() == TargetType.PROJECT) {
            messagingTemplate.convertAndSend(Topic.PROJECT_CHANGED.getPath(), event);
        }
    }
}
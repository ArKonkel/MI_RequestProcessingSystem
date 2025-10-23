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

    /**
     * Sends a user notification based on the specified event.
     * The method determines the type of the notification and sends
     * it to the appropriate messaging topic.
     *
     * @param event the user notification event containing information
     *              about the type of notification, the target process item,
     *              and any additional details for creating the notification
     */
    @Override
    public void sendUserNotification(UserNotificationEvent event) {
        log.info("Sending notification event {}", event);

        if (event.type() == UserNotificationType.ASSIGNED) {
            messagingTemplate.convertAndSend(Topic.PROCESS_ITEM_ASSIGNED.getPath(), event);

        } else if (event.type() == UserNotificationType.COMMENT_MENTIONING) {
            messagingTemplate.convertAndSend(Topic.IN_COMMENT_MENTIONED.getPath(), event);

        } else if (event.type() == UserNotificationType.INCOMING_REQUEST) {
            messagingTemplate.convertAndSend(Topic.INCOMING_REQUEST.getPath(), event);
        }
    }

    /**
     * Sends a change notification based on the provided event. The method identifies
     * the target type of the change and broadcasts the event to the corresponding
     * messaging topic.
     *
     * @param event the change notification event containing details about the type
     *              of change, the target entity of the change, and additional
     *              information to notify relevant subscribers
     */
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
package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.notification;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@Slf4j
@AllArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    private final ApplicationEventPublisher publisher;


    /**
     * Handles a change notification event after the transaction has been committed.
     *
     * @param event the change notification event containing details about the change,
     *              including process item ID, change type, and target type
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onChange(ChangeNotificationEvent event) {
        performChangeNotification(event);
    }

    /**
     * Handles a user notification event after the transaction has been committed.
     *
     * @param event the user notification event containing details such as the notification type,
     *              process item ID, title, list of user IDs to notify, notification text, timestamp, and target type
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onUserNotification(UserNotificationEvent event) {
        performUserNotification(event);
    }

    /**
     * Sends a user notification event by publishing it through the application event publisher.
     *
     * @param event the user notification event containing details such as notification type,
     *              process item ID, title, list of user IDs to notify, notification text,
     *              timestamp, and target type
     */
    @Override
    public void sendUserNotification(UserNotificationEvent event) {
        log.info("Publish notification event {}", event);

        publisher.publishEvent(event);
    }

    /**
     * Sends a change notification event by publishing it through the application event publisher.
     *
     * @param event the change notification event containing details such as process item ID,
     *              change type, and target type
     */
    @Override
    public void sendChangeNotification(ChangeNotificationEvent event) {
        log.info("Publish change message event {}", event);

        publisher.publishEvent(event);
    }


    /**
     * Handles a user notification event by routing it to the appropriate messaging topic
     * based on the type of the notification. The type of the notification determines
     * which topic the event should be sent to for broadcasting updates.
     *
     * @param event the user notification event containing details such as the notification type,
     *              process item ID, process item title, list of user IDs to notify, notification text,
     *              timestamp, and target type
     */
    private void performUserNotification(UserNotificationEvent event){
        log.info("Perform user notification event {}", event);

        if (event.type() == UserNotificationType.ASSIGNED) {
            messagingTemplate.convertAndSend(Topic.PROCESS_ITEM_ASSIGNED.getPath(), event);

        } else if (event.type() == UserNotificationType.COMMENT_MENTIONING) {
            messagingTemplate.convertAndSend(Topic.IN_COMMENT_MENTIONED.getPath(), event);

        } else if (event.type() == UserNotificationType.INCOMING_REQUEST) {
            messagingTemplate.convertAndSend(Topic.INCOMING_REQUEST.getPath(), event);
        }

    }

    /**
     * Handles a change notification event by routing it to the appropriate messaging topic
     * based on the target type of the event. The target type determines which topic the
     * event should be sent to for broadcasting updates.
     *
     * @param event the change notification event containing details such as the process
     *              item ID, change type, and target type
     */
    private void performChangeNotification(ChangeNotificationEvent event){
        log.info("Perform change message event {}", event);

        if (event.targetType() == TargetType.CUSTOMER_REQUEST) {
            messagingTemplate.convertAndSend(Topic.CUSTOMER_REQUEST_CHANGED.getPath(), event);

        } else if (event.targetType() == TargetType.TASK) {
            messagingTemplate.convertAndSend(Topic.TASK_CHANGED.getPath(), event);

        } else if (event.targetType() == TargetType.PROJECT) {
            messagingTemplate.convertAndSend(Topic.PROJECT_CHANGED.getPath(), event);
        }
    }
}
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
    public void sendNotification(NotificationEvent event) {
        log.info("Sending notification event {}", event);

        if (event.type() == NotificationType.ASSIGNED) {
            messagingTemplate.convertAndSend(Topic.PROCESS_ITEM_ASSIGNED.getPath(), event);

        } else if (event.type() == NotificationType.COMMENT_MENTIONING) {
            messagingTemplate.convertAndSend(Topic.IN_COMMENT_MENTIONED.getPath(), event);
        }
    }
}
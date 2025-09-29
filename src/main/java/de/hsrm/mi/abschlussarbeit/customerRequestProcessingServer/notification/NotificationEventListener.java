package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.notification;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class NotificationEventListener {

    private final NotificationService notificationService;

    @EventListener
    public void handleNotificationEvent(NotificationEvent event) {
        log.info("Handling notification event {}", event);

        this.notificationService.sendNotification(event);
    }
}
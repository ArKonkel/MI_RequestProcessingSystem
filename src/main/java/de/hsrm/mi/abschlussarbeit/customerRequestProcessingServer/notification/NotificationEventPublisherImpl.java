package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.notification;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class NotificationEventPublisherImpl implements NotificationEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void publishNotificationEvent(NotificationEvent event) {
        log.info("Publishing notification event {}", event);

        this.applicationEventPublisher.publishEvent(event);
    }
}

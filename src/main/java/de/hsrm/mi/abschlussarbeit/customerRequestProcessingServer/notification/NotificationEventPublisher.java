package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.notification;

public interface NotificationEventPublisher {

    void publishNotificationEvent(NotificationEvent event);
}

package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.notification;

public interface NotificationService {

    void sendUserNotification(UserNotificationEvent event);

    void sendChangeNotification(ChangeNotificationEvent event);
}

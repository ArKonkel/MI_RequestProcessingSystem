package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.notification;

import java.time.Instant;
import java.util.List;

public record NotificationEvent(NotificationType type, String processItemTitle, List<Long> userIds, String text,
                                Instant timeStamp) {
}
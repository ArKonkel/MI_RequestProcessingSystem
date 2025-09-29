package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.notification;

import java.time.Instant;
import java.util.List;

public record NotificationEvent(NotificationType type, Long processItemId, String processItemTitle, List<Long> userIdsToNotify, String text,
                                Instant timeStamp) {
}
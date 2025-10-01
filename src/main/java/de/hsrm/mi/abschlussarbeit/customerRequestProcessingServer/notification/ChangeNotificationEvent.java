package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.notification;

public record ChangeNotificationEvent(Long processItemId, ChangeType changeType, TargetType targetType) {
}

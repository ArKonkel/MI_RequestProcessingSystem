package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.integration.outlook.graphTypes;

import java.util.List;

public record Message(String subject, ItemBody body, List<Recipient> toRecipients) {
}

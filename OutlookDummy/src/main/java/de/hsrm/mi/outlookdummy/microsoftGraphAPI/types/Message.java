package de.hsrm.mi.outlookdummy.microsoftGraphAPI.types;

import java.util.List;

public record Message(String subject, ItemBody body, List<Recipient> toRecipients) {
}

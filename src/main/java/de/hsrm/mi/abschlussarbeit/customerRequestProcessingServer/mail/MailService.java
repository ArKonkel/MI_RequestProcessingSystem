package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.mail;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.integration.outlook.graphTypes.SendMailRequest;

import java.util.List;

public interface MailService {
    void sendMails(SendMailRequest mail, String senderMail, List<EmailAddress> recipients);

}

package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.mail;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.customerRequest.CustomerRequest;

import java.util.List;

public interface MailService {
    void sendMails(CustomerRequest request, List<EmailAddress> emailAddresses);
}

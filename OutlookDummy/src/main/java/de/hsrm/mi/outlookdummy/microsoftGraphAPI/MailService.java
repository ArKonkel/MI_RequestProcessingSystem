package de.hsrm.mi.outlookdummy.microsoftGraphAPI;

import de.hsrm.mi.outlookdummy.microsoftGraphAPI.types.SendMailRequest;

public interface MailService {

    void sendMail(String fromPrincipal, SendMailRequest request);
}

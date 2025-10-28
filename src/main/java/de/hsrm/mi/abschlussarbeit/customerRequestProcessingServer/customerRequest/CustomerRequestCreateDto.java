package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.customerRequest;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.mail.EmailAddress;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.processItem.ProcessItemCreateDto;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.shared.Priority;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerRequestCreateDto {
    private String contactFirstName;
    private String contactLastName;
    private String contactPhoneNumber;
    private ProcessItemCreateDto processItem;
    private Priority priority;
    private Category category;
    private String programNumber;
    private String module;
    private Long customerId;
    private List<EmailAddress> toRecipients;
}
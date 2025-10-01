package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.customerRequest;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.customer.CustomerDto;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.processItem.ProcessItemDto;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.shared.Priority;
import lombok.Data;

@Data
public class CustomerRequestDto {
    ProcessItemDto processItem;
    Priority priority;
    Long estimatedScope;
    CustomerRequestStatus status;
    Chargeable chargeable;
    Category category;
    CustomerDto customer;
}

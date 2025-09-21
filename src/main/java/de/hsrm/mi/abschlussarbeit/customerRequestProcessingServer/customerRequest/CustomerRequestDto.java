package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.customerRequest;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.processItem.ProcessItemDto;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.processItem.Priority;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.customer.CustomerDto;

public record CustomerRequestDto(
        ProcessItemDto processItem,
        Priority priority,
        Long estimatedScope,
        CustomerRequestStatus status,
        Chargeable chargeable,
        Category category,
        CustomerDto customer
) {
}

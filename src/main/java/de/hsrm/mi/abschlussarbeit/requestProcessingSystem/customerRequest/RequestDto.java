package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.customerRequest;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.processItem.ProcessItemDto;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.processItem.Priority;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.customer.CustomerDto;

public record RequestDto(
        ProcessItemDto processItem,
        Priority priority,
        Long estimatedScope,
        RequestStatus status,
        Chargeable chargeable,
        Category category,
        CustomerDto customer
) {
}

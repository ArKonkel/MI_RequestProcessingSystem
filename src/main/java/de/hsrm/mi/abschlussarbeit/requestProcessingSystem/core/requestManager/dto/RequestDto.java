package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.requestManager.dto;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.dto.ProcessItemDto;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.enums.Category;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.enums.Chargeable;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.enums.Priority;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.enums.RequestStatus;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.customerManager.dto.CustomerDto;

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

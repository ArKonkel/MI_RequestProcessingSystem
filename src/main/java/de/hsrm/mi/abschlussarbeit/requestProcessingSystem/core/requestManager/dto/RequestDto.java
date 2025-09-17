package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.requestManager.dto;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.dto.ProcessItemDto;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.enums.Category;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.enums.Priority;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.enums.RequestStatus;

public record RequestDto(
        ProcessItemDto processItem,
        Priority priority,
        Long estimatedScope,
        RequestStatus status,
        boolean chargeable,
        Category category,
        Long customerId
) {
}

package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.requestManager.dto;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.dto.CreateProcessItemDto;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.enums.Category;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.enums.Priority;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class RequestCreateDto {
    private CreateProcessItemDto processItem;
    private Priority priority;
    private Category category;
    private Long customerId;
}
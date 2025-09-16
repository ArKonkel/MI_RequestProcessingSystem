package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.requestManager.dto;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.dto.CreateProcessItemDto;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.enums.Priority;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CreateRequestDto {
    private CreateProcessItemDto processItem;
    private Priority priority;
    private Long categoryId;
    private Long customerId;
}
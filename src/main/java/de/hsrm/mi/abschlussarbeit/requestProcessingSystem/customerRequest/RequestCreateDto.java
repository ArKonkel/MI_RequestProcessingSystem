package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.customerRequest;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.processItem.ProcessItemCreateDto;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.processItem.Priority;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class RequestCreateDto {
    private ProcessItemCreateDto processItem;
    private Priority priority;
    private Category category;
    private Long customerId;
}
package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.customerRequest;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.processItem.ProcessItemCreateDto;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.processItem.Priority;
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
package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.customerRequest;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.shared.Priority;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.shared.TimeUnit;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class UpdateCustomerRequestDto {
    private String title;
    private String description;
    private Long assigneeId;
    private Priority priority;
    private BigDecimal estimatedScope;
    private TimeUnit scopeUnit;
    private CustomerRequestStatus status;
    private Chargeable chargeable;
    private Category category;
}

package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.customerRequest;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.customer.CustomerDto;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.processItem.ProcessItemDto;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.shared.Priority;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.shared.TimeUnit;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CustomerRequestDto {
    ProcessItemDto processItem;
    Priority priority;
    BigDecimal estimatedScope;
    TimeUnit scopeUnit;
    CustomerRequestStatus status;
    Chargeable chargeable;
    Category category;
    CustomerDto customer;
}

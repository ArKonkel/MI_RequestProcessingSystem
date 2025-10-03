package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.customerRequest;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.processItem.ProcessItemMapper;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.customer.CustomerMapper;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.task.TaskMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper class for {@link CustomerRequest} and {@link CustomerRequestDto}.
 */
@Mapper(componentModel = "spring", uses = {TaskMapper.class, ProcessItemMapper.class, CustomerMapper.class})
public interface CustomerRequestMapper {

    @Mapping(target = "processItem", source = ".")
    @Mapping(target = "category", source = "category")
    CustomerRequestDto toDto(CustomerRequest request);

    @Mapping(target = "estimatedScope", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "chargeable", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "assignee", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    @Mapping(target = "projects", ignore = true)
    @Mapping(target = "category", source = "category")
    @Mapping(target = "scopeUnit", ignore = true)
    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "title", source = "processItem.title")
    @Mapping(target = "description", source = "processItem.description")
    CustomerRequest toEntity(CustomerRequestCreateDto requestDto);
}



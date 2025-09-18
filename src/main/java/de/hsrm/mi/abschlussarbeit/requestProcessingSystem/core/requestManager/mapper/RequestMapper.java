package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.requestManager.mapper;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.mapper.ProcessItemMapper;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.requestManager.dto.RequestCreateDto;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.requestManager.dto.RequestDto;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.requestManager.entity.Request;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.customerManager.mapper.CustomerMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper class for {@link Request} and {@link RequestDto}.
 */
@Mapper(componentModel = "spring", uses = {ProcessItemMapper.class, CustomerMapper.class})
public interface RequestMapper {

    @Mapping(target = "processItem", source = ".")
    @Mapping(target = "category", source = "category")
    RequestDto toDto(Request request);

    @Mapping(target = "estimatedScope", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "chargeable", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationDate", ignore = true) //must be set manually
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "assignee", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    @Mapping(target = "projects", ignore = true)
    @Mapping(target = "category", source = "category")
    @Mapping(target = "customer", ignore = true) //is handled in RequestService
    @Mapping(target = "title", source = "processItem.title")
    @Mapping(target = "description", source = "processItem.description")
    Request toEntity(RequestCreateDto requestDto);
}



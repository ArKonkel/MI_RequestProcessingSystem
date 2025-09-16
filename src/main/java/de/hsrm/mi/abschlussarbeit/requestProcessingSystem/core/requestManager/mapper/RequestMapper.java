package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.requestManager.mapper;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.mapper.ProcessItemMapper;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.requestManager.dto.CreateRequestDto;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.requestManager.dto.RequestDto;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.requestManager.entity.Request;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper class for {@link Request} and {@link RequestDto}.
 */
@Mapper(componentModel = "spring", uses = {ProcessItemMapper.class})
public interface RequestMapper {

    @Mapping(target = "processItem", source = ".")
    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "customerId", source = "customer.id")
    RequestDto toDto(Request request);

    @Mapping(target = "estimatedScope", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "chargeable", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationDate", ignore = true) //must be set manually
    @Mapping(target = "customer.id", source = "customerId")
    @Mapping(target = "category.id", source = "categoryId")
    @Mapping(target = "title", source = "processItem.title")
    @Mapping(target = "description", source = "processItem.description")
    Request toEntity(CreateRequestDto requestDto);
}


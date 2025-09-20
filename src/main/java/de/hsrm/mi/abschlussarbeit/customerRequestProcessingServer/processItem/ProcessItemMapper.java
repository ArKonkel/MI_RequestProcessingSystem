package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.processItem;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for {@link ProcessItem} and {@link ProcessItemDto}.
 */
@Mapper(componentModel = "spring")
public interface ProcessItemMapper {

    @Mapping(target = "assigneeId", source = "assignee.id")
    ProcessItemDto toDto(ProcessItem processItem);
}

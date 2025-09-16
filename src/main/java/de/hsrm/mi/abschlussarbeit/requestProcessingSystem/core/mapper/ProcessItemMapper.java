package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.mapper;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.dto.ProcessItemDto;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.entity.ProcessItem;
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

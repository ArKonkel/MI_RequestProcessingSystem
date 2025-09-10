package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.statusManager.mapper;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.statusManager.dto.StatusDto;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.statusManager.entity.Status;
import org.mapstruct.Mapper;

/**
 * Mapper for {@link Status} and {@link StatusDto}.
 */
@Mapper(componentModel = "spring")
public interface StatusMapper {

    StatusDto toDto(Status status);
}

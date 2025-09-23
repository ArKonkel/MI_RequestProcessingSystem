package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.expertise;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for {@link Expertise} and {@link ExpertiseDto}}
 */
@Mapper(componentModel = "spring")
public interface ExpertiseMapper {

    ExpertiseDto toDto(Expertise expertise);

    @Mapping(target = "tasks", ignore = true)
    Expertise toEntity(ExpertiseDto expertiseDto);
}

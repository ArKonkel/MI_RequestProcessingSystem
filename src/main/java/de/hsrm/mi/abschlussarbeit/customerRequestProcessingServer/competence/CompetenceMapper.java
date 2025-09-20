package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.competence;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for {@link Competence} and {@link CompetenceDto}}
 */
@Mapper(componentModel = "spring")
public interface CompetenceMapper {

    @Mapping(target = "type", expression = "java(mapType(competence))")
    CompetenceDto toDto(Competence competence);

    default CompetenceType mapType(Competence competence) {
        if (competence instanceof Qualification) {
            return CompetenceType.QUALIFICATION;
        } else if (competence instanceof Expertise) {
            return CompetenceType.EXPERTISE;
        } else if (competence instanceof Responsibility) {
            return CompetenceType.RESPONSIBILITY;
        }
        throw new IllegalArgumentException("Unknown competences type: " + competence.getClass());
    }
}

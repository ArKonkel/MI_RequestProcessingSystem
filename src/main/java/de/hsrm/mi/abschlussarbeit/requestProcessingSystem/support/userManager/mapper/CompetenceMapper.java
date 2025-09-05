package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.userManager.mapper;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.enums.CompetenceType;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.userManager.dto.CompetenceDto;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.userManager.entity.Competence;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.userManager.entity.Expertise;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.userManager.entity.Qualification;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.userManager.entity.Responsibility;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

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
        throw new IllegalArgumentException("Unknown competence type: " + competence.getClass());
    }
}

package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.competence;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ObjectFactory;

/**
 * Mapper for {@link Competence} and {@link CompetenceDto}}
 */
@Mapper(componentModel = "spring")
public interface CompetenceMapper {

    @Mapping(target = "type", expression = "java(mapType(competence))")
    CompetenceDto toDto(Competence competence);

    @Mapping(target = "employees", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    Competence toEntity(CompetenceDto dto);

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

    //Because Competence is an abstract class, it needs to create instances
    @ObjectFactory
    default Competence createCompetence(CompetenceDto dto) {
        if (dto == null) return null;

        switch (dto.type()) {
            case QUALIFICATION -> {
                Qualification q = new Qualification();
                q.setId(dto.id());
                q.setName(dto.name());
                q.setDescription(dto.description());
                return q;
            }
            case EXPERTISE -> {
                Expertise e = new Expertise();
                e.setId(dto.id());
                e.setName(dto.name());
                e.setDescription(dto.description());
                return e;
            }
            case RESPONSIBILITY -> {
                Responsibility r = new Responsibility();
                r.setId(dto.id());
                r.setName(dto.name());
                r.setDescription(dto.description());
                return r;
            }
            default -> throw new IllegalArgumentException("Unknown type " + dto.type());
        }
    }

}

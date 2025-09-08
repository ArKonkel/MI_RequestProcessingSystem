package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.userManager.mapper;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.enums.CompetenceType;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.userManager.dto.CompetenceDto;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.userManager.entity.Expertise;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.userManager.entity.Qualification;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.userManager.entity.Responsibility;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CompetenceMapperTest {

    private final CompetenceMapper competenceMapper = Mappers.getMapper(CompetenceMapper.class);

    @Test
    void testQualificationMapping() {
        //GIVEN
        Qualification qualification = new Qualification();
        qualification.setId(1L);
        qualification.setName("Bachelor");
        qualification.setDescription("Academic degree");

        //WHEN
        CompetenceDto dto = competenceMapper.toDto(qualification);

        //THEN
        assertEquals(1L, dto.id());
        assertEquals("Bachelor", dto.name());
        assertEquals("Academic degree", dto.description());
        assertEquals(CompetenceType.QUALIFICATION, dto.type());
    }

    @Test
    void testExpertiseMapping() {
        Expertise expertise = new Expertise();
        expertise.setId(2L);
        expertise.setName("Java Programming");
        expertise.setDescription("Expert in Java");

        CompetenceDto dto = competenceMapper.toDto(expertise);

        assertEquals(2L, dto.id());
        assertEquals("Java Programming", dto.name());
        assertEquals("Expert in Java", dto.description());
        assertEquals(CompetenceType.EXPERTISE, dto.type());
    }

    @Test
    void testResponsibilityMapping() {
        Responsibility responsibility = new Responsibility();
        responsibility.setId(3L);
        responsibility.setName("Team Lead");
        responsibility.setDescription("Leads a team");

        CompetenceDto dto = competenceMapper.toDto(responsibility);

        assertEquals(3L, dto.id());
        assertEquals("Team Lead", dto.name());
        assertEquals("Leads a team", dto.description());
        assertEquals(CompetenceType.RESPONSIBILITY, dto.type());
    }

}
package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.expertise;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class ExpertiseMapperTest {

    private final ExpertiseMapper expertiseMapper = Mappers.getMapper(ExpertiseMapper.class);


    @Test
    void testExpertiseMapping() {
        Expertise expertise = new Expertise();
        expertise.setId(2L);
        expertise.setName("Java Programming");
        expertise.setDescription("Expert in Java");

        ExpertiseDto dto = expertiseMapper.toDto(expertise);

        assertEquals(2L, dto.id());
        assertEquals("Java Programming", dto.name());
        assertEquals("Expert in Java", dto.description());
    }
}
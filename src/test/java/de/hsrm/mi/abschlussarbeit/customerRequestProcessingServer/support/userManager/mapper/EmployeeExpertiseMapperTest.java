package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.support.userManager.mapper;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.employee.EmployeeExpertiseMapper;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.employee.ExpertiseLevel;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.employee.EmployeeExpertiseDto;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.employee.Employee;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.employee.EmployeeExpertise;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.expertise.Expertise;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class EmployeeExpertiseMapperTest {

    @Autowired
    private EmployeeExpertiseMapper employeeExpertiseMapper;


    @Test
    void toDto() {
        // GIVEN
        Employee employee = new Employee();
        employee.setId(42L);

        Expertise expertise = new Expertise();
        expertise.setId(99L);
        expertise.setName("Customizing");
        expertise.setDescription("Software anpassen");

        EmployeeExpertise employeeExpertise = new EmployeeExpertise(
                1L,
                employee,
                expertise,
                ExpertiseLevel.ADVANCED
        );

        // WHEN
        EmployeeExpertiseDto dto = employeeExpertiseMapper.toDto(employeeExpertise);

        // THEN
        assertNotNull(dto);
        assertEquals(1L, dto.id());
        assertEquals(42L, dto.employeeId());
        assertEquals(ExpertiseLevel.ADVANCED, dto.level());

        CompetenceDto competenceDto = dto.expertise();
        assertNotNull(competenceDto);
        assertEquals(99L, competenceDto.id());
        assertEquals("Customizing", competenceDto.name());
        assertEquals("Software anpassen", competenceDto.description());
        assertEquals(CompetenceType.EXPERTISE, competenceDto.type());
    }
}
package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.employee;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.expertise.Expertise;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.expertise.ExpertiseDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

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

        ExpertiseDto expertiseDto = dto.expertise();
        assertNotNull(expertiseDto);
        assertEquals(99L, expertiseDto.id());
        assertEquals("Customizing", expertiseDto.name());
        assertEquals("Software anpassen", expertiseDto.description());
    }
}
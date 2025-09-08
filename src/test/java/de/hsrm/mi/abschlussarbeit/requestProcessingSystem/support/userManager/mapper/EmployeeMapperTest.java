package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.userManager.mapper;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.calendarModule.entity.Calendar;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.enums.ExpertiseLevel;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.userManager.dto.EmployeeDto;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.userManager.dto.EmployeeExpertiseDto;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.userManager.entity.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class EmployeeMapperTest {

    @Autowired
    private EmployeeMapper employeeMapper;

    @Test
    void toDto() {
        // GIVEN
        Department department = new Department();
        department.setId(10L);

        User user = new User();
        user.setId(20L);

        Calendar calendar = new Calendar();
        calendar.setId(30L);

        Employee employee = new Employee();
        employee.setId(1L);
        employee.setFirstName("Max");
        employee.setLastName("Mustermann");
        employee.setEmail("max@test.de");
        employee.setPhoneNumber("123456");
        employee.setHireDate(LocalDate.of(2025, 9, 4));

        employee.setDepartment(department);
        employee.setUser(user);
        employee.setCalendar(calendar);

        // Expertise + Kompetenz
        Expertise expertise = new Expertise();
        expertise.setId(100L);
        expertise.setName("Customizing");
        expertise.setDescription("Software anpassen");

        EmployeeExpertise employeeExpertise = new EmployeeExpertise(
                200L,
                employee,
                expertise,
                ExpertiseLevel.EXPERT
        );
        employee.setEmployeeExpertise(Set.of(employeeExpertise));

        // WHEN
        EmployeeDto dto = employeeMapper.toDto(employee);

        // THEN
        assertNotNull(dto);
        assertEquals(1L, dto.id());
        assertEquals("Max", dto.firstName());
        assertEquals("Mustermann", dto.lastName());
        assertEquals("max@test.de", dto.email());
        assertEquals("123456", dto.phoneNumber());
        assertEquals(LocalDate.of(2025, 9, 4), dto.hireDate());

        assertEquals(10L, dto.departmentId());
        assertEquals(20L, dto.userId());
        assertEquals(30L, dto.calendarId());

        assertNotNull(dto.employeeExpertise());
        assertEquals(1, dto.employeeExpertise().size());

        EmployeeExpertiseDto expertiseDto = dto.employeeExpertise().iterator().next();
        assertEquals(200L, expertiseDto.id());
        assertEquals(1L, expertiseDto.employeeId());
        assertEquals(ExpertiseLevel.EXPERT, expertiseDto.level());

        assertNotNull(expertiseDto.expertise());
        assertEquals(100L, expertiseDto.expertise().id());
        assertEquals("Customizing", expertiseDto.expertise().name());
    }
}

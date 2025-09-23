package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.user;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.employee.Employee;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Test
    void toDto() {
        // GIVEN
        Employee employee = new Employee();
        employee.setId(42L);

        User user = new User();
        user.setId(1L);
        user.setName("Max Mustermann");
        user.setDescription("Blub");
        user.setEmployee(employee);

        // WHEN
        UserDto dto = userMapper.toDto(user);

        // THEN
        assertNotNull(dto);
        assertEquals(1L, dto.id());
        assertEquals("Max Mustermann", dto.name());
        assertEquals("Blub", dto.description());
        assertEquals(42L, dto.employeeId());
    }
}

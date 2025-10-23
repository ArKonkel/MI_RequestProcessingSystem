package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.user;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.employee.Employee;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    void toDto() {
        // GIVEN
        Employee employee = new Employee();
        employee.setId(42L);

        User user = new User();
        user.setId(1L);
        user.setName("Max Mustermann");
        user.setEmployee(employee);

        // WHEN
        UserDto dto = userMapper.toDto(user);

        // THEN
        assertNotNull(dto);
        assertEquals(1L, dto.id());
        assertEquals("Max Mustermann", dto.name());
    }
}

package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.capacity;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.employee.Employee;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.employee.EmployeeDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

//Needs to be a SpringBootTest because it has dependencies to other mappers
@SpringBootTest
class CapacityMapperTest {

    @Autowired
    private CapacityMapper capacityMapper;

    @Test
    void testMappingMatchingEmployeeCapacitiesVOtoDto() {
        // GIVEN
        Employee employee = new Employee();
        employee.setId(1L);
        employee.setFirstName("Max");
        employee.setLastName("Mustermann");

        CalculatedCapacityCalendarEntryVO entryVO = new CalculatedCapacityCalendarEntryVO(
                "Task A", LocalDate.of(2025, 9, 21), 120L
        );

        CalculatedCapacitiesOfMatchVO matchVO = new CalculatedCapacitiesOfMatchVO(
                employee, 7L, true, List.of(entryVO)
        );

        MatchingEmployeeCapacitiesVO vo = new MatchingEmployeeCapacitiesVO(
                10L, List.of(matchVO)
        );

        // WHEN
        MatchingEmployeeCapacitiesDto dto = capacityMapper.toDto(vo);

        // THEN
        assertEquals(vo.taskId(), dto.getTaskId());
        assertEquals(1, dto.getMatchCalculationResult().size());

        CalculatedCapacitiesOfMatchDto matchDto = dto.getMatchCalculationResult().get(0);

        EmployeeDto employeeDto = new EmployeeDto(
                employee.getId(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getEmail(),
                employee.getWorkingHoursPerDay(),
                null,
                null,
                null,
                null
        );

        assertEquals(employeeDto, matchDto.getEmployee());
        assertEquals(7L, matchDto.getExpertisePoints());
        assertEquals(true, matchDto.getCanCompleteTaskEarliest());
        assertEquals(1, matchDto.getCalculatedCalendarCapacities().size());

        CalculatedCapacityCalendarEntryDto entryDto = matchDto.getCalculatedCalendarCapacities().get(0);
        assertEquals("Task A", entryDto.getTitle());
        assertEquals(LocalDate.of(2025, 9, 21), entryDto.getDate());
        assertEquals(120L, entryDto.getDurationInMinutes());
    }
}

package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.resourceCapacityPlanner.service;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.dto.ProcessItemDto;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.enums.Priority;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.resourceCapacityPlanner.dto.CalculatedCapacityCalendarEntryDto;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.resourceCapacityPlanner.dto.MatchCalculationResultDto;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.resourceCapacityPlanner.dto.MatchingEmployeeForTaskDto;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.taskManager.dto.TaskDto;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.userManager.dto.EmployeeDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ResourceCapacityServiceImplTest {
    @Mock
    CapacityCalculatorEngine capacityCalculatorEngine;

    @Mock
    TaskMatcher taskMatcher;

    @InjectMocks
    ResourceCapacityServiceImpl resourceCapacityService;



    @Test
    void findBestMatches() {
        // GIVEN
        String taskTitle = "Customizing the Software";
        Long estimatedTime = 120L;
        LocalDate dueDate = LocalDate.parse("2025-09-12");
        TaskDto taskDto = createTaskDto(1L, taskTitle, estimatedTime, dueDate);

        // Employees
        EmployeeDto employee1 = createEmployeeDto(1L, "Max", "Mustermann", BigDecimal.valueOf(8), 10L);
        EmployeeDto employee2 = createEmployeeDto(2L, "Sabine", "Mustermann", BigDecimal.valueOf(8), 20L);

        // Competence points
        Map<EmployeeDto, Integer> competenceMatches = Map.of(
                employee1, 80,
                employee2, 70
        );

        // Calculated capacities
        List<CalculatedCapacityCalendarEntryDto> capacitiesEmployee1 = List.of(
                createCalculatedCapacityEntryDto("Task Slot 1", LocalDate.parse("2025-09-08"), 60L),
                createCalculatedCapacityEntryDto("Task Slot 2", LocalDate.parse("2025-09-09"), 60L)
        );
        List<CalculatedCapacityCalendarEntryDto> capacitiesEmployee2 = List.of(
                createCalculatedCapacityEntryDto("Task Slot 1", LocalDate.parse("2025-09-09"), 120L)
        );

        // Employees who can complete earliest
        List<EmployeeDto> earliestEmployees = List.of(employee1);

        // Mocking dependencies
        Mockito.when(taskMatcher.findBestMatchingEmployees(taskDto)).thenReturn(competenceMatches);
        Mockito.when(capacityCalculatorEngine.calculateFreeCapacity(taskDto, employee1.id(), LocalDate.now(), dueDate))
                .thenReturn(capacitiesEmployee1);
        Mockito.when(capacityCalculatorEngine.calculateFreeCapacity(taskDto, employee2.id(), LocalDate.now(), dueDate))
                .thenReturn(capacitiesEmployee2);
        Mockito.when(capacityCalculatorEngine.calculateEmployeesAbleToCompleteTaskEarliest(Mockito.anyMap()))
                .thenReturn(earliestEmployees);

        // WHEN
        MatchingEmployeeForTaskDto result = resourceCapacityService.findBestMatches(taskDto);

        // THEN
        assertEquals(taskDto, result.task());
        assertEquals(2, result.matchCalculationResult().size());

        MatchCalculationResultDto resultEmployee1 = result.matchCalculationResult().stream()
                .filter(res -> res.employee().equals(employee1))
                .findFirst()
                .orElseThrow();

        assertEquals(80L, resultEmployee1.competencePoints());
        assertTrue(resultEmployee1.canCompleteTaskEarliest());
        assertEquals(capacitiesEmployee1, resultEmployee1.calculatedCalendarCapacities());

        MatchCalculationResultDto resultEmployee2 = result.matchCalculationResult().stream()
                .filter(res -> res.employee().equals(employee2))
                .findFirst()
                .orElseThrow();

        assertEquals(70L, resultEmployee2.competencePoints());
        assertFalse(resultEmployee2.canCompleteTaskEarliest());
        assertEquals(capacitiesEmployee2, resultEmployee2.calculatedCalendarCapacities());
    }


    private CalculatedCapacityCalendarEntryDto createCalculatedCapacityEntryDto(String title, LocalDate date, Long duration){
        return new CalculatedCapacityCalendarEntryDto(
                title,
                date,
                duration
        );
    }

    private EmployeeDto createEmployeeDto(Long id, String firstname, String lastname, BigDecimal workingTime, Long calendarId) {
        return new EmployeeDto(
                id,
                firstname,
                lastname,
                "",
                "",
                null,
                workingTime,
                null,
                null,
                null,
                null,
                calendarId
        );
    }

    private TaskDto createTaskDto(Long id, String title, Long estimatedTime, LocalDate dueDate) {
        ProcessItemDto processItem = new ProcessItemDto(
                id,
                title,
                "",
                dueDate,
                null,
                null,
                Set.of()
        );

        return new TaskDto(
                processItem,
                estimatedTime,
                dueDate,
                Priority.MEDIUM,
                null,
                Set.of(),
                null,
                null,
                null,
                Set.of(),
                null,
                null
        );
    }
}
package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.core.resourceCapacityPlanner.service;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.capacity.*;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.processItem.ProcessItemDto;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.processItem.Priority;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.task.TaskDto;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.task.TaskManager;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.competence.CompetenceType;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.competence.CompetenceDto;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.employee.EmployeeDto;
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

    @Mock
    TaskManager taskManager;

    @InjectMocks
    ResourceCapacityServiceImpl resourceCapacityService;


    @Test
    void findBestMatches_shouldFindMatches() {
        // GIVEN
        String taskTitle = "Customizing the Software";
        Long estimatedTime = 120L;
        LocalDate dueDate = LocalDate.now().plusDays(14);
        Long taskId = 1L;
        TaskDto taskDto = createTaskDto(taskId, taskTitle, estimatedTime, dueDate);

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
        Mockito.when(taskManager.getTaskById(taskId)).thenReturn(taskDto);
        Mockito.when(taskMatcher.findBestMatchingEmployees(taskDto)).thenReturn(competenceMatches);
        Mockito.when(capacityCalculatorEngine.calculateFreeCapacity(taskDto, employee1.id(), LocalDate.now(), dueDate))
                .thenReturn(capacitiesEmployee1);
        Mockito.when(capacityCalculatorEngine.calculateFreeCapacity(taskDto, employee2.id(), LocalDate.now(), dueDate))
                .thenReturn(capacitiesEmployee2);
        Mockito.when(capacityCalculatorEngine.calculateEmployeesAbleToCompleteTaskEarliest(Mockito.anyMap()))
                .thenReturn(earliestEmployees);

        // WHEN
        MatchingEmployeeForTaskDto result = resourceCapacityService.findBestMatches(taskId);

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

    @Test
    void findBestMatches_ShouldThrowTaskNotReadyForResourcePlanningException_DueDateInPast() {
        // GIVEN
        String taskTitle = "Customizing the Software";
        Long estimatedTime = 120L;
        LocalDate dueDate = LocalDate.parse("1999-09-12"); // due date in the past
        Long taskId = 1L;
        TaskDto taskDto = createTaskDto(taskId, taskTitle, estimatedTime, dueDate);

        // WHEN + THEN
        Mockito.when(taskManager.getTaskById(taskId)).thenReturn(taskDto);

        assertThrows(
                TaskNotReadyForResourcePlanningException.class,
                () -> resourceCapacityService.findBestMatches(taskId)
        );
    }

    @Test
    void findBestMatches_ShouldThrowTaskNotReadyForResourcePlanningException_NoEstimation() {
        // GIVEN
        String taskTitle = "Customizing the Software";
        Long estimatedTime = 0L;
        LocalDate dueDate = LocalDate.parse("3000-09-12");
        Long taskId = 1L;
        TaskDto taskDto = createTaskDto(taskId, taskTitle, estimatedTime, dueDate);

        // WHEN + THEN
        Mockito.when(taskManager.getTaskById(taskId)).thenReturn(taskDto);

        assertThrows(
                TaskNotReadyForResourcePlanningException.class,
                () -> resourceCapacityService.findBestMatches(taskId)
        );
    }

    @Test
    void findBestMatches_ShouldThrowTaskNotReadyForResourcePlanningException_NoCompetence() {
        // GIVEN
        String taskTitle = "Customizing the Software";
        Long estimatedTime = 120L;
        LocalDate dueDate = LocalDate.parse("3000-09-12");
        Long taskId = 1L;
        TaskDto taskDto = createTaskDtoWithoutCompetence(taskId, taskTitle, estimatedTime, dueDate);

        // WHEN + THEN
        Mockito.when(taskManager.getTaskById(taskId)).thenReturn(taskDto);

        assertThrows(
                TaskNotReadyForResourcePlanningException.class,
                () -> resourceCapacityService.findBestMatches(taskId)
        );
    }




    private CalculatedCapacityCalendarEntryDto createCalculatedCapacityEntryDto(String title, LocalDate date, Long duration) {
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
        CompetenceDto competenceDto = new CompetenceDto(1L, "Customizing", "", CompetenceType.EXPERTISE);

        ProcessItemDto processItem = new ProcessItemDto(
                id,
                title,
                "",
                dueDate,
                null
        );

        return new TaskDto(
                processItem,
                estimatedTime,
                0L,
                dueDate,
                Priority.MEDIUM,
                "",
                null,
                null,
                Set.of(competenceDto),
                null,
                null,
                null,
                null,
                null
        );
    }

    private TaskDto createTaskDtoWithoutCompetence(Long id, String title, Long estimatedTime, LocalDate dueDate) {

        ProcessItemDto processItem = new ProcessItemDto(
                id,
                title,
                "",
                dueDate,
                null
        );

        return new TaskDto(
                processItem,
                estimatedTime,
                0L,
                dueDate,
                Priority.MEDIUM,
                "",
                null,
                null,
                Set.of(),
                null,
                null,
                null,
                null,
                null
        );
    }
}
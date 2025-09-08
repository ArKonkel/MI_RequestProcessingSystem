package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.resourceCapacityPlanner.service;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.dto.ProcessItemDto;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.enums.Priority;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.taskManager.dto.TaskDto;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.calendarModule.dto.CalendarDto;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.calendarModule.dto.CalendarEntryDto;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.calendarModule.service.CalendarModule;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.userManager.dto.EmployeeDto;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.userManager.service.UserManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class CapacityCalculatorEngineImplTest {

    @Mock
    CalendarModule calendarModule;

    @Mock
    UserManager userManager;

    @InjectMocks
    private CapacityCalculatorEngineImpl capacityCalculatorEngine;

    /*
        LOGIK:
        - Nur von Montag bis Freitag
        - Nur max. Stundenanzahl, die der Mitarbeiter am Tag arbeitet (8)
        - Die freie Zeit für den nächsten Tag berechnen, wenn da Kapazität.
        - Due date beachten
    */

    @Test
    void calculateNextFreeCapacity_TaskFitsOneDay() {
        // GIVEN
        Long employeeId = 1L;
        BigDecimal employeeWorkingHoursPerDay = BigDecimal.valueOf(8); // 8h working day
        Long estimatedTaskDuration = 240L; // 4h new task

        LocalDate firstDay = LocalDate.parse("2025-09-08");
        LocalDate lastDayToCheck = firstDay.plusDays(2); // range: 3 days

        // task, that need to be planned
        TaskDto taskDto = createTaskDto(
                10L,
                "Customizing the Software",
                estimatedTaskDuration,
                LocalDate.parse("2025-11-05")
        );

        // Already given entries:
        // Day 1: two meetings 2h = 4h
        CalendarEntryDto entry1 = createEntryDto(10L, firstDay, 120L);
        CalendarEntryDto entry2 = createEntryDto(20L, firstDay, 120L);

        // Day 2: 8h-Meeting = Full day
        CalendarEntryDto entry3 = createEntryDto(30L, firstDay.plusDays(1), 480L);

        CalendarDto calendarDto = new CalendarDto(
                99L,
                List.of(entry1, entry2, entry3),
                employeeId
        );

        EmployeeDto employeeDto = createEmployeeDto(
                employeeId,
                "Max",
                "Mustermann",
                employeeWorkingHoursPerDay,
                99L
        );

        // WHEN
        Mockito.when(userManager.getEmployeeById(employeeId)).thenReturn(employeeDto);
        Mockito.when(calendarModule.getCalendarOfEmployee(employeeId, firstDay, lastDayToCheck))
                .thenReturn(calendarDto);

        List<CalendarEntryDto> result =
                capacityCalculatorEngine.calculateNextFreeCapacity(taskDto, employeeId, firstDay, lastDayToCheck);

        // THEN
        // Expected: Task should fit in first day (4h are free)
        CalendarEntryDto expectedEntry = new CalendarEntryDto(
                null,                               // no Id, because its calculated
                taskDto.processItem().title(),
                taskDto.processItem().description(),
                firstDay,
                taskDto.estimatedTime()
        );

        assertEquals(List.of(expectedEntry), result);
    }


    @Test
    void testCalculateNextFreeCapacity_TaskFitsSeveralDays() {
        // GIVEN
        Long employeeId = 1L;
        BigDecimal employeeWorkingHoursPerDay = BigDecimal.valueOf(8); // 8h working day
        Long estimatedTaskDuration = 300L; // 4h new task

        LocalDate firstDay = LocalDate.parse("2025-09-08");
        LocalDate lastDayToCheck = firstDay.plusDays(2); // range: 3 days

        // task, that need to be planned
        TaskDto taskDto = createTaskDto(
                10L,
                "Customizing the Software",
                estimatedTaskDuration,
                LocalDate.parse("2025-11-05")
        );

        // Already given entries:
        // Day 1: two meetings 3h = 6h: 2 free
        CalendarEntryDto entry1 = createEntryDto(10L, firstDay, 180L);
        CalendarEntryDto entry2 = createEntryDto(20L, firstDay, 180L);

        // Day 2: 4h-Meeting: 4h free
        CalendarEntryDto entry3 = createEntryDto(30L, firstDay.plusDays(1), 240L);

        CalendarDto calendarDto = new CalendarDto(
                99L,
                List.of(entry1, entry2, entry3),
                employeeId
        );

        Long taskOccupiedTimeFirstDay = 120L; //2h
        Long taskOccupiedTimeSecondDay = 180L; //3h

        EmployeeDto employeeDto = createEmployeeDto(
                employeeId,
                "Max",
                "Mustermann",
                employeeWorkingHoursPerDay,
                99L
        );

        // WHEN
        Mockito.when(userManager.getEmployeeById(employeeId)).thenReturn(employeeDto);
        Mockito.when(calendarModule.getCalendarOfEmployee(employeeId, firstDay, lastDayToCheck))
                .thenReturn(calendarDto);

        List<CalendarEntryDto> result =
                capacityCalculatorEngine.calculateNextFreeCapacity(taskDto, employeeId, firstDay, lastDayToCheck);

        // THEN
        // Expected: Task should fit first day 2h and second day 2h
        CalendarEntryDto expectedEntryFirstDay = new CalendarEntryDto(
                null,                               // no Id, because its calculated
                taskDto.processItem().title(),
                taskDto.processItem().description(),
                firstDay,
                taskOccupiedTimeFirstDay
        );

        CalendarEntryDto expectedEntrySecondDay = new CalendarEntryDto(
                null,                               // no Id, because its calculated
                taskDto.processItem().title(),
                taskDto.processItem().description(),
                firstDay.plusDays(1),
                taskOccupiedTimeSecondDay
        );

        assertEquals(List.of(expectedEntryFirstDay, expectedEntrySecondDay), result);
    }

    @Test
    void testCalculateNextFreeCapacity_OnlyMondayUntilFriday() {

    }

    @Test
    void testCalculateNextFreeCapacity_FridayToMonday() {

    }

    private CalendarEntryDto createEntryDto(Long id, LocalDate date, Long duration) {
        return new CalendarEntryDto(
                id,
                "Meeting",
                "Meeting with the team",
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
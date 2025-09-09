package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.resourceCapacityPlanner.service;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.dto.ProcessItemDto;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.enums.Priority;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.resourceCapacityPlanner.dto.CalculatedCalendarEntryDto;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.resourceCapacityPlanner.exception.NoCapacityUntilDueDateException;
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
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
    void calculateFreeCapacity_TaskFitsOneDay() {
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

        List<CalculatedCalendarEntryDto> result =
                capacityCalculatorEngine.calculateFreeCapacity(taskDto, employeeId, firstDay, lastDayToCheck);

        // THEN
        // Expected: Task should fit in first day (4h are free)
        CalculatedCalendarEntryDto expectedEntry = new CalculatedCalendarEntryDto(
                taskDto.processItem().title(),
                firstDay,
                taskDto.estimatedTime()
        );

        assertEquals(List.of(expectedEntry), result);
    }


    @Test
    void testCalculateFreeCapacity_TaskFitsSeveralDays() {
        // GIVEN
        Long employeeId = 1L;
        BigDecimal employeeWorkingHoursPerDay = BigDecimal.valueOf(8); // 8h working day
        Long estimatedTaskDuration = 300L; // 5h new task

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
        // Day 1: one meeting 6h: 2h free
        CalendarEntryDto entry1 = createEntryDto(10L, firstDay, 6 * 60L);

        // Day 2: one meeting 6h: 2h free
        CalendarEntryDto entry2 = createEntryDto(20L, firstDay.plusDays(1), 6 * 60L);

        // Day 3:one meeting 4h: 4h free
        CalendarEntryDto entry3 = createEntryDto(30L, firstDay.plusDays(2), 4 * 60L);

        CalendarDto calendarDto = new CalendarDto(
                99L,
                List.of(entry1, entry2, entry3),
                employeeId
        );

        Long taskOccupiedTimeFirstDay = 120L; //2h
        Long taskOccupiedTimeSecondDay = 120L; //2h
        Long taskOccupiedTimeThirdDay = 60L; //1h

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

        List<CalculatedCalendarEntryDto> result =
                capacityCalculatorEngine.calculateFreeCapacity(taskDto, employeeId, firstDay, lastDayToCheck);

        // THEN
        // Expected: Task should fit first day 2h and second day 2h
        CalculatedCalendarEntryDto expectedEntryFirstDay = new CalculatedCalendarEntryDto(
                taskDto.processItem().title(),
                firstDay,
                taskOccupiedTimeFirstDay
        );

        CalculatedCalendarEntryDto expectedEntrySecondDay = new CalculatedCalendarEntryDto(
                taskDto.processItem().title(),
                firstDay.plusDays(1),
                taskOccupiedTimeSecondDay
        );

        CalculatedCalendarEntryDto expectedEntryThirdDay = new CalculatedCalendarEntryDto(
                taskDto.processItem().title(),
                firstDay.plusDays(2),
                taskOccupiedTimeThirdDay
        );

        assertEquals(List.of(expectedEntryFirstDay, expectedEntrySecondDay, expectedEntryThirdDay), result);
    }

    @Test
    void testCalculateFreeCapacity_throwNoCapacityUntilDueDateException() {
        // GIVEN
        Long employeeId = 1L;
        BigDecimal employeeWorkingHoursPerDay = BigDecimal.valueOf(8); // 8h working day
        Long estimatedTaskDuration = 300L; // 4h new task


        LocalDate firstDay = LocalDate.parse("2025-09-08");
        LocalDate lastDayToCheck = firstDay.plusDays(2); // range: 3 days
        LocalDate dueDate = lastDayToCheck;

        // task, that need to be planned
        TaskDto taskDto = createTaskDto(
                10L,
                "Customizing the Software",
                estimatedTaskDuration,
                dueDate
        );

        // Already given entries:
        // Day 1: One Meeting = 6h: 2h free
        CalendarEntryDto entry1 = createEntryDto(10L, firstDay, 6 * 60L);

        // Day 2: 8h-Meeting: 0h free
        CalendarEntryDto entry2 = createEntryDto(30L, firstDay.plusDays(1), 8 * 60L);

        //Day 3: 7h-Meeting: 1h free
        CalendarEntryDto entry3 = createEntryDto(30L, firstDay.plusDays(2), 7 * 60L);

        CalendarDto calendarDto = new CalendarDto(
                99L,
                List.of(entry1, entry2, entry3),
                employeeId
        );

        Long taskOccupiedTimeFirstDay = 120L; //2h
        Long taskOccupiedTimeSecondDay = 0L; //0h
        Long taskOccupiedTimeThirdDay = 60L; //1h

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

        //Excepted: Throws Exception, because in the given range is no capacity for the task
        assertThrows(
                NoCapacityUntilDueDateException.class,
                () -> capacityCalculatorEngine.calculateFreeCapacity(taskDto, employeeId, firstDay, dueDate)
        );

    }


    @Test
    void testCalculateFreeCapacity_shouldSplitBetweenFridayAndMonday() {
        // GIVEN
        Long employeeId = 1L;
        BigDecimal employeeWorkingHoursPerDay = BigDecimal.valueOf(8); // 8h working day
        Long estimatedTaskDuration = 300L; // 5h new task

        LocalDate friday = LocalDate.parse("2025-09-12"); //Friday
        LocalDate monday = LocalDate.parse("2025-09-15"); //Monday after Weekend

        // task, that need to be planned
        TaskDto taskDto = createTaskDto(
                10L,
                "Customizing the Software",
                estimatedTaskDuration,
                LocalDate.parse("2025-11-05")
        );

        // Already given entries:
        // Friday: two meetings 3h = 6h: 2h free
        CalendarEntryDto entry1 = createEntryDto(10L, friday, 180L);
        CalendarEntryDto entry2 = createEntryDto(20L, friday, 180L);

        // Monday: 4h-Meeting: 4h free
        CalendarEntryDto entry3 = createEntryDto(30L, monday, 240L);

        CalendarDto calendarDto = new CalendarDto(
                99L,
                List.of(entry1, entry2, entry3),
                employeeId
        );

        Long taskOccupiedTimeFriday = 120L; //2h
        Long taskOccupiedTimeMonday = 180L; //3h

        EmployeeDto employeeDto = createEmployeeDto(
                employeeId,
                "Max",
                "Mustermann",
                employeeWorkingHoursPerDay,
                99L
        );

        // WHEN
        Mockito.when(userManager.getEmployeeById(employeeId)).thenReturn(employeeDto);
        Mockito.when(calendarModule.getCalendarOfEmployee(employeeId, friday, monday))
                .thenReturn(calendarDto);

        List<CalculatedCalendarEntryDto> result =
                capacityCalculatorEngine.calculateFreeCapacity(taskDto, employeeId, friday, monday);

        // THEN
        // Expected: Task should fit Friday 2h and Monday 2h
        CalculatedCalendarEntryDto expectedFriday = new CalculatedCalendarEntryDto(
                taskDto.processItem().title(),
                friday,
                taskOccupiedTimeFriday
        );

        CalculatedCalendarEntryDto expectedMonday = new CalculatedCalendarEntryDto(
                taskDto.processItem().title(),
                monday,
                taskOccupiedTimeMonday
        );

        assertEquals(List.of(expectedFriday, expectedMonday), result);
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

    private CalculatedCalendarEntryDto createCalculatedEntryDto(String title, LocalDate date, Long duration){
        return new CalculatedCalendarEntryDto(
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

    @Test
    void calculateEmployeesAbleToCompleteEarliest_OneFit() {
        //GIVEN
        BigDecimal employeeWorkingHoursPerDay = BigDecimal.valueOf(8); // 8h working day

        LocalDate firstDay = LocalDate.parse("2025-09-08");
        LocalDate secondDay = firstDay.plusDays(1);
        LocalDate thirdDay = firstDay.plusDays(2);
        String taskTitle = "Customizing the Software";

        CalculatedCalendarEntryDto employee1entry1 = createCalculatedEntryDto(taskTitle, firstDay, 180L);
        CalculatedCalendarEntryDto employee1entry2 = createCalculatedEntryDto(taskTitle, secondDay, 180L);
        CalculatedCalendarEntryDto employee1entry3 = createCalculatedEntryDto(taskTitle, thirdDay, 240L);

        CalculatedCalendarEntryDto employee2entry1 = createCalculatedEntryDto(taskTitle, firstDay, 180L);
        CalculatedCalendarEntryDto employee2entry2 = createCalculatedEntryDto(taskTitle, secondDay, 120L);

        CalculatedCalendarEntryDto employee3entry1 = createCalculatedEntryDto(taskTitle, firstDay, 180L);
        CalculatedCalendarEntryDto employee3entry2 = createCalculatedEntryDto(taskTitle, secondDay, 180L); //needs longer than employee 2

        EmployeeDto employeeDto1 = createEmployeeDto(
                1L,
                "Max",
                "Mustermann",
                employeeWorkingHoursPerDay,
                null
        );

        EmployeeDto employeeDto2 = createEmployeeDto(
                2L,
                "Sabine",
                "Mustermann",
                employeeWorkingHoursPerDay,
                null
        );

        EmployeeDto employeeDto3 = createEmployeeDto(
                2L,
                "Erich",
                "Mustermann",
                employeeWorkingHoursPerDay,
                null
        );

        List<CalculatedCalendarEntryDto> employee1TaskEntries = List.of(employee1entry1, employee1entry2, employee1entry3);
        List<CalculatedCalendarEntryDto> employee2TaskEntries = List.of(employee2entry1, employee2entry2);
        List<CalculatedCalendarEntryDto> employee3TaskEntries = List.of(employee3entry1, employee3entry2);

        Map<EmployeeDto, List<CalculatedCalendarEntryDto>> employeeTaskEntries = Map.of(
                employeeDto1, employee1TaskEntries,
                employeeDto2, employee2TaskEntries,
                employeeDto3, employee3TaskEntries);

        //WHEN
        List<EmployeeDto> result = capacityCalculatorEngine.calculateEmployeesAbleToCompleteTaskEarliest(employeeTaskEntries);

        //THEN
        //Second employee should be chosen because he can finish the task earliest
        assertEquals(List.of(employeeDto2), result);

    }

    @Test
    void calculateEmployeesAbleToCompleteEarliest_SeveralFits() {
        //GIVEN
        BigDecimal employeeWorkingHoursPerDay = BigDecimal.valueOf(8); // 8h working day
        String taskTitle = "Customizing the Software";

        LocalDate firstDay = LocalDate.parse("2025-09-08");
        LocalDate secondDay = firstDay.plusDays(1);
        LocalDate thirdDay = firstDay.plusDays(2);

        CalculatedCalendarEntryDto employee1entry1 = createCalculatedEntryDto(taskTitle, firstDay, 180L);
        CalculatedCalendarEntryDto employee1entry2 = createCalculatedEntryDto(taskTitle, secondDay, 180L);
        CalculatedCalendarEntryDto employee1entry3 = createCalculatedEntryDto(taskTitle, thirdDay, 240L);

        CalculatedCalendarEntryDto employee2entry1 = createCalculatedEntryDto(taskTitle, firstDay, 180L);
        CalculatedCalendarEntryDto employee2entry2 = createCalculatedEntryDto(taskTitle, secondDay, 180L);

        CalculatedCalendarEntryDto employee3entry1 = createCalculatedEntryDto(taskTitle, firstDay, 180L);
        CalculatedCalendarEntryDto employee3entry2 = createCalculatedEntryDto(taskTitle, secondDay, 180L); //needs same time as employee 1

        EmployeeDto employeeDto1 = createEmployeeDto(
                1L,
                "Max",
                "Mustermann",
                employeeWorkingHoursPerDay,
                null
        );

        EmployeeDto employeeDto2 = createEmployeeDto(
                2L,
                "Sabine",
                "Mustermann",
                employeeWorkingHoursPerDay,
                null
        );

        EmployeeDto employeeDto3 = createEmployeeDto(
                2L,
                "Erich",
                "Mustermann",
                employeeWorkingHoursPerDay,
                null
        );

        List<CalculatedCalendarEntryDto> employee1TaskEntries = List.of(employee1entry1, employee1entry2, employee1entry3);
        List<CalculatedCalendarEntryDto> employee2TaskEntries = List.of(employee2entry1, employee2entry2);
        List<CalculatedCalendarEntryDto> employee3TaskEntries = List.of(employee3entry1, employee3entry2);

        Map<EmployeeDto, List<CalculatedCalendarEntryDto>> employeeTaskEntries = Map.of(
                employeeDto1, employee1TaskEntries,
                employeeDto2, employee2TaskEntries,
                employeeDto3, employee3TaskEntries);

        //WHEN
        List<EmployeeDto> result = capacityCalculatorEngine.calculateEmployeesAbleToCompleteTaskEarliest(employeeTaskEntries);

        //THEN
        //Second employee should be chosen because he can finish the task earliest
        assertEquals(List.of(employeeDto2, employeeDto3), result);

    }
}
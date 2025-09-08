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
        //GIVEN
        Long employeeId = 1L;
        BigDecimal employeeWorkingTime = BigDecimal.valueOf(8);
        Long estimatedTime = 240L; //4 hours

        LocalDate entryDate = LocalDate.parse("2025-09-08");;
        LocalDate entriesTo = entryDate.plusDays(2);
        LocalDate dueDate = LocalDate.parse("2025-11-05");

        //Task
        TaskDto taskDto = createTaskDto(10L,
                "Customizing the Software",
                estimatedTime,
                dueDate);


        //Two hour meeting
        Long durationEntry1 = 120L;
        CalendarEntryDto entry1 = createEntryDto(10L, entryDate, durationEntry1);

        //Two hour meeting same day
        Long durationEntry2 = 120L;
        CalendarEntryDto entry2 = createEntryDto(20L, entryDate, durationEntry2);

        //eight hour meeting next day
        Long durationEntry3 = 480L;
        CalendarEntryDto entry3 = createEntryDto(30L, entryDate.plusDays(1), durationEntry3);


        CalendarDto calendarDto = new CalendarDto(
                99L,
                List.of(entry1, entry2, entry3),
                employeeId
        );

        EmployeeDto employeeDto = createEmployeeDto(employeeId, "Max", "Mustermann", employeeWorkingTime, 99L);

        //WHEN
        Mockito.when(userManager.getEmployeeById(employeeId)).thenReturn(employeeDto);
        Mockito.when(calendarModule.getCalendarOfEmployee(employeeId, entryDate, entriesTo)).thenReturn(calendarDto);

        List<CalendarEntryDto> result = capacityCalculatorEngine.calculateNextFreeCapacity(taskDto, employeeId, entryDate, entriesTo);

        //THEN
        //Long calculatedBusyTime = employeeWorkingTime.longValue() - (entry1.duration() + entry2.duration());
        CalendarEntryDto expectedCalendarEntryDto = new CalendarEntryDto(
                null, taskDto.processItem().title(), taskDto.processItem().description(), entryDate, taskDto.estimatedTime()//id null because it is calculated
        );

        List<CalendarEntryDto> expectedCalendarEntryDtos = List.of(expectedCalendarEntryDto);

        assertEquals(expectedCalendarEntryDtos, result);
    }

    @Test
    void testCalculateNextFreeCapacity_TaskFitsSeveralDays() {

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
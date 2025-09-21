package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.core.resourceCapacityPlanner.service;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.calendar.CalendarDto;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.calendar.CalendarEntryDto;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.calendar.CalendarModule;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.capacity.*;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.competence.CompetenceDto;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.competence.CompetenceType;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.employee.EmployeeDto;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.employee.EmployeeExpertiseDto;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.employee.ExpertiseLevel;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.processItem.Priority;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.processItem.ProcessItemDto;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.task.TaskDto;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.task.TaskManager;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.user.UserManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CapacityServiceImplTest {
    @Mock
    TaskManager taskManager;

    @Mock
    UserManager userManager;

    @Mock
    CalendarModule calendarModule;

    @Spy
    @InjectMocks
    CapacityServiceImpl resourceCapacityService;


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

        //stubbing own functions (Spy)
        doReturn(competenceMatches)
                .when(resourceCapacityService)
                .findBestMatchingEmployees(taskDto);

        doReturn(capacitiesEmployee1)
                .when(resourceCapacityService)
                .calculateFreeCapacity(taskDto, employee1.id(), LocalDate.now(), dueDate);

        doReturn(capacitiesEmployee2)
                .when(resourceCapacityService)
                .calculateFreeCapacity(taskDto, employee2.id(), LocalDate.now(), dueDate);

        doReturn(earliestEmployees)
                .when(resourceCapacityService)
                .calculateEmployeesAbleToCompleteTaskEarliest(Mockito.anyMap());

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

    @Test
    void findBestMatchingEmployees() {
        // GIVEN
        CompetenceDto expertise1 = new CompetenceDto(1L, "Customizing", "", CompetenceType.EXPERTISE);
        CompetenceDto expertise2 = new CompetenceDto(2L, "Coding", "", CompetenceType.EXPERTISE);
        CompetenceDto expertise3 = new CompetenceDto(3L, "Printing", "", CompetenceType.EXPERTISE);

        TaskDto taskDto = createTaskDto(1L, "Customizing der Software", 120L, LocalDate.parse("2025-11-05"),
                Priority.HIGH, Set.of(expertise1, expertise3));

        EmployeeExpertiseDto employee1Expertise1 = new EmployeeExpertiseDto(1L, 1L, expertise1, ExpertiseLevel.ADVANCED);
        EmployeeExpertiseDto employee1Expertise3 = new EmployeeExpertiseDto(2L, 1L, expertise3, ExpertiseLevel.EXPERT);
        EmployeeExpertiseDto employee2Expertise1 = new EmployeeExpertiseDto(3L, 2L, expertise1, ExpertiseLevel.BEGINNER);
        EmployeeExpertiseDto employee2Expertise2 = new EmployeeExpertiseDto(4L, 2L, expertise2, ExpertiseLevel.EXPERT);

        EmployeeDto employee1 = createEmployee(1L, "Max", Set.of(
                employee2Expertise1, employee2Expertise2
        ));

        EmployeeDto employee2 = createEmployee(2L, "Sabine", Set.of(
                employee1Expertise1, employee1Expertise3
        ));

        EmployeeDto employee3 = createEmployee(3L, "Freddy", Set.of());


        Map<EmployeeDto, Integer> expected = Map.of(
                employee1, 7, //Employee1 has a sum of 7 points because he has expertise1 and expertise3
                employee2, 1
        );

        // WHEN
        when(userManager.getAllEmployeeExpertises()).thenReturn(List.of(employee1Expertise1, employee1Expertise3, employee2Expertise1, employee2Expertise2));
        when(userManager.getEmployeesByIds(List.of(employee1.id(), employee2.id()))).thenReturn(List.of(employee1, employee2));

        Map<EmployeeDto, Integer> result = resourceCapacityService.findBestMatchingEmployees(taskDto);

        // THEN
        assertEquals(expected, result);
    }

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

        List<CalculatedCapacityCalendarEntryDto> result =
                resourceCapacityService.calculateFreeCapacity(taskDto, employeeId, firstDay, lastDayToCheck);

        // THEN
        // Expected: Task should fit in first day (4h are free)
        CalculatedCapacityCalendarEntryDto expectedEntry = new CalculatedCapacityCalendarEntryDto(
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

        List<CalculatedCapacityCalendarEntryDto> result =
                resourceCapacityService.calculateFreeCapacity(taskDto, employeeId, firstDay, lastDayToCheck);

        // THEN
        // Expected: Task should fit first day 2h and second day 2h
        CalculatedCapacityCalendarEntryDto expectedEntryFirstDay = new CalculatedCapacityCalendarEntryDto(
                taskDto.processItem().title(),
                firstDay,
                taskOccupiedTimeFirstDay
        );

        CalculatedCapacityCalendarEntryDto expectedEntrySecondDay = new CalculatedCapacityCalendarEntryDto(
                taskDto.processItem().title(),
                firstDay.plusDays(1),
                taskOccupiedTimeSecondDay
        );

        CalculatedCapacityCalendarEntryDto expectedEntryThirdDay = new CalculatedCapacityCalendarEntryDto(
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
                () -> resourceCapacityService.calculateFreeCapacity(taskDto, employeeId, firstDay, dueDate)
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

        List<CalculatedCapacityCalendarEntryDto> result =
                resourceCapacityService.calculateFreeCapacity(taskDto, employeeId, friday, monday);

        // THEN
        // Expected: Task should fit Friday 2h and Monday 2h
        CalculatedCapacityCalendarEntryDto expectedFriday = new CalculatedCapacityCalendarEntryDto(
                taskDto.processItem().title(),
                friday,
                taskOccupiedTimeFriday
        );

        CalculatedCapacityCalendarEntryDto expectedMonday = new CalculatedCapacityCalendarEntryDto(
                taskDto.processItem().title(),
                monday,
                taskOccupiedTimeMonday
        );

        assertEquals(List.of(expectedFriday, expectedMonday), result);
    }

    @Test
    void calculateEmployeesAbleToCompleteEarliest_OneFit() {
        //GIVEN
        BigDecimal employeeWorkingHoursPerDay = BigDecimal.valueOf(8); // 8h working day

        LocalDate firstDay = LocalDate.parse("2025-09-08");
        LocalDate secondDay = firstDay.plusDays(1);
        LocalDate thirdDay = firstDay.plusDays(2);
        String taskTitle = "Customizing the Software";

        CalculatedCapacityCalendarEntryDto employee1entry1 = createCalculatedEntryDto(taskTitle, firstDay, 180L);
        CalculatedCapacityCalendarEntryDto employee1entry2 = createCalculatedEntryDto(taskTitle, secondDay, 180L);
        CalculatedCapacityCalendarEntryDto employee1entry3 = createCalculatedEntryDto(taskTitle, thirdDay, 240L);

        CalculatedCapacityCalendarEntryDto employee2entry1 = createCalculatedEntryDto(taskTitle, firstDay, 180L);
        CalculatedCapacityCalendarEntryDto employee2entry2 = createCalculatedEntryDto(taskTitle, secondDay, 120L);

        CalculatedCapacityCalendarEntryDto employee3entry1 = createCalculatedEntryDto(taskTitle, firstDay, 180L);
        CalculatedCapacityCalendarEntryDto employee3entry2 = createCalculatedEntryDto(taskTitle, secondDay, 180L); //needs longer than employee 2

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

        List<CalculatedCapacityCalendarEntryDto> employee1TaskEntries = List.of(employee1entry1, employee1entry2, employee1entry3);
        List<CalculatedCapacityCalendarEntryDto> employee2TaskEntries = List.of(employee2entry1, employee2entry2);
        List<CalculatedCapacityCalendarEntryDto> employee3TaskEntries = List.of(employee3entry1, employee3entry2);

        Map<EmployeeDto, List<CalculatedCapacityCalendarEntryDto>> employeeTaskEntries = Map.of(
                employeeDto1, employee1TaskEntries,
                employeeDto2, employee2TaskEntries,
                employeeDto3, employee3TaskEntries);

        //WHEN
        List<EmployeeDto> result = resourceCapacityService.calculateEmployeesAbleToCompleteTaskEarliest(employeeTaskEntries);

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

        CalculatedCapacityCalendarEntryDto employee1entry1 = createCalculatedEntryDto(taskTitle, firstDay, 180L);
        CalculatedCapacityCalendarEntryDto employee1entry2 = createCalculatedEntryDto(taskTitle, secondDay, 180L);
        CalculatedCapacityCalendarEntryDto employee1entry3 = createCalculatedEntryDto(taskTitle, thirdDay, 240L);

        CalculatedCapacityCalendarEntryDto employee2entry1 = createCalculatedEntryDto(taskTitle, firstDay, 180L);
        CalculatedCapacityCalendarEntryDto employee2entry2 = createCalculatedEntryDto(taskTitle, secondDay, 180L);

        CalculatedCapacityCalendarEntryDto employee3entry1 = createCalculatedEntryDto(taskTitle, firstDay, 180L);
        CalculatedCapacityCalendarEntryDto employee3entry2 = createCalculatedEntryDto(taskTitle, secondDay, 180L); //needs same time as employee 1

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

        List<CalculatedCapacityCalendarEntryDto> employee1TaskEntries = List.of(employee1entry1, employee1entry2, employee1entry3);
        List<CalculatedCapacityCalendarEntryDto> employee2TaskEntries = List.of(employee2entry1, employee2entry2);
        List<CalculatedCapacityCalendarEntryDto> employee3TaskEntries = List.of(employee3entry1, employee3entry2);

        Map<EmployeeDto, List<CalculatedCapacityCalendarEntryDto>> employeeTaskEntries = Map.of(
                employeeDto1, employee1TaskEntries,
                employeeDto2, employee2TaskEntries,
                employeeDto3, employee3TaskEntries);

        //WHEN
        List<EmployeeDto> result = resourceCapacityService.calculateEmployeesAbleToCompleteTaskEarliest(employeeTaskEntries);

        //THEN
        //Second employee should be chosen because he can finish the task earliest
        assertEquals(List.of(employeeDto2, employeeDto3), result);

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

    private CalculatedCapacityCalendarEntryDto createCalculatedEntryDto(String title, LocalDate date, Long duration) {
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

    private CalculatedCapacityCalendarEntryDto createCalculatedCapacityEntryDto(String title, LocalDate date, Long duration) {
        return new CalculatedCapacityCalendarEntryDto(
                title,
                date,
                duration
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

    private EmployeeDto createEmployee(long id, String vorname, Set<EmployeeExpertiseDto> expertise) {
        return new EmployeeDto(id, vorname, "Nachname", "email@test.de",
                null, null, null, expertise, Set.of(), null, null, null);
    }

    private TaskDto createTaskDto(long id, String title, Long estimatedTime, LocalDate dueDate, Priority priority, Set<CompetenceDto> competenceIds) {
        ProcessItemDto processItem = new ProcessItemDto(
                id,
                title,
                "",
                LocalDate.parse("2025-09-05"),
                null
        );

        return new TaskDto(
                processItem,
                estimatedTime,
                0L,
                dueDate,
                priority,
                "",
                null,
                null,
                competenceIds,
                null,
                null,
                null,
                null,
                null
        );

    }

}
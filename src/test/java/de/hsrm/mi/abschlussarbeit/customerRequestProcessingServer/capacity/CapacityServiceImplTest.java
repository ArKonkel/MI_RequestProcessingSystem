package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.capacity;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.calendar.Calendar;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.calendar.CalendarEntry;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.calendar.CalendarService;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.competence.Competence;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.competence.Expertise;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.employee.Employee;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.employee.EmployeeExpertise;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.employee.EmployeeService;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.employee.ExpertiseLevel;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.processItem.Priority;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.task.Task;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.task.TaskService;
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
    TaskService taskService;

    @Mock
    EmployeeService employeeService;

    @Mock
    CalendarService calendarService;

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
        Task task = createTask(taskId, taskTitle, estimatedTime, dueDate);

        // Employees
        Employee employee1 = new Employee();
        employee1.setId(1L);
        employee1.setFirstName("Max");
        employee1.setLastName("Mustermann");
        employee1.setWorkingHoursPerDay(BigDecimal.valueOf(8));

        Employee employee2 = new Employee();
        employee2.setId(2L);
        employee2.setFirstName("Sabine");
        employee2.setLastName("Mustermann");
        employee2.setWorkingHoursPerDay(BigDecimal.valueOf(8));

        // Competence points
        Map<Employee, Integer> competenceMatches = Map.of(
                employee1, 80,
                employee2, 70
        );

        // Calculated capacities
        List<CalculatedCapacityCalendarEntryVO> capacitiesEmployee1 = List.of(
                createCalculatedCapacityEntryDto("Task Slot 1", LocalDate.parse("2025-09-08"), 60L),
                createCalculatedCapacityEntryDto("Task Slot 2", LocalDate.parse("2025-09-09"), 60L)
        );
        List<CalculatedCapacityCalendarEntryVO> capacitiesEmployee2 = List.of(
                createCalculatedCapacityEntryDto("Task Slot 1", LocalDate.parse("2025-09-09"), 120L)
        );

        // Employees who can complete earliest
        List<Employee> earliestEmployees = List.of(employee1);

        // Mocking dependencies
        when(taskService.getTaskById(taskId)).thenReturn(task);

        //stubbing own functions (Spy)
        doReturn(competenceMatches)
                .when(resourceCapacityService)
                .findBestMatchingEmployees(task);

        doReturn(capacitiesEmployee1)
                .when(resourceCapacityService)
                .calculateFreeCapacity(task, employee1.getId(), LocalDate.now(), dueDate);

        doReturn(capacitiesEmployee2)
                .when(resourceCapacityService)
                .calculateFreeCapacity(task, employee2.getId(), LocalDate.now(), dueDate);

        doReturn(earliestEmployees)
                .when(resourceCapacityService)
                .calculateEmployeesAbleToCompleteTaskEarliest(Mockito.anyMap());

        // WHEN
        MatchingEmployeeCapacitiesVO result = resourceCapacityService.findBestMatches(taskId);

        // THEN
        assertEquals(task.getId(), result.taskId());
        assertEquals(2, result.matchCalculationResult().size());

        CalculatedCapacitiesOfMatchVO resultEmployee1 = result.matchCalculationResult().stream()
                .filter(res -> res.employee().equals(employee1))
                .findFirst()
                .orElseThrow();

        assertEquals(80L, resultEmployee1.competencePoints());
        assertTrue(resultEmployee1.canCompleteTaskEarliest());
        assertEquals(capacitiesEmployee1, resultEmployee1.calculatedCalendarCapacities());

        CalculatedCapacitiesOfMatchVO resultEmployee2 = result.matchCalculationResult().stream()
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
        Task task = createTask(taskId, taskTitle, estimatedTime, dueDate);

        // WHEN + THEN
        when(taskService.getTaskById(taskId)).thenReturn(task);

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
        Task task = createTask(taskId, taskTitle, estimatedTime, dueDate);

        // WHEN + THEN
        when(taskService.getTaskById(taskId)).thenReturn(task);

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

        Task task = new Task();
        task.setId(taskId);
        task.setEstimatedTime(estimatedTime);
        task.setDueDate(dueDate);
        task.setTitle(taskTitle);

        // WHEN + THEN
        when(taskService.getTaskById(taskId)).thenReturn(task);

        assertThrows(
                TaskNotReadyForResourcePlanningException.class,
                () -> resourceCapacityService.findBestMatches(taskId)
        );
    }

    @Test
    void findBestMatchingEmployees() {
        // GIVEN
        Expertise expertise1 = new Expertise();
        expertise1.setId(1L);
        expertise1.setName("Customizing");

        Expertise expertise2 = new Expertise();
        expertise2.setId(2L);
        expertise2.setName("Coding");

        Expertise expertise3 = new Expertise();
        expertise3.setId(3L);
        expertise3.setName("Printing");

        Task task = createTask(1L, "Customizing der Software", 120L, LocalDate.parse("2025-11-05"),
                Priority.HIGH, Set.of(expertise1, expertise3));

        // Employees
        Employee employee1 = new Employee();
        employee1.setId(1L);
        employee1.setFirstName("Max");

        Employee employee2 = new Employee();
        employee2.setId(2L);
        employee2.setFirstName("Sabine");

        Employee employee3 = new Employee();
        employee3.setId(3L);
        employee3.setFirstName("Peter");

        // EmployeeExpertise
        EmployeeExpertise ee1 = new EmployeeExpertise();
        ee1.setId(1L);
        ee1.setEmployee(employee1);
        ee1.setExpertise(expertise1);
        ee1.setLevel(ExpertiseLevel.ADVANCED);

        EmployeeExpertise ee2 = new EmployeeExpertise();
        ee2.setId(2L);
        ee2.setEmployee(employee1);
        ee2.setExpertise(expertise3);
        ee2.setLevel(ExpertiseLevel.EXPERT);

        EmployeeExpertise ee3 = new EmployeeExpertise();
        ee3.setId(3L);
        ee3.setEmployee(employee2);
        ee3.setExpertise(expertise1);
        ee3.setLevel(ExpertiseLevel.BEGINNER);

        EmployeeExpertise ee4 = new EmployeeExpertise();
        ee4.setId(4L);
        ee4.setEmployee(employee2);
        ee4.setExpertise(expertise2);
        ee4.setLevel(ExpertiseLevel.EXPERT);

        // Expected Result
        Map<Employee, Integer> expected = Map.of(
                employee1, 7, // Expertise1 + Expertise3
                employee2, 1
        );

        // WHEN
        when(employeeService.getAllEmployeeExpertises())
                .thenReturn(List.of(ee1, ee2, ee3, ee4));

        Map<Employee, Integer> result = resourceCapacityService.findBestMatchingEmployees(task);

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
        Task task = createTask(
                10L,
                "Customizing the Software",
                estimatedTaskDuration,
                LocalDate.parse("2025-11-05")
        );

        // Already given entries:
        // Day 1: two meetings 2h = 4h
        CalendarEntry entry1 = createEntry(10L, firstDay, 120L);
        CalendarEntry entry2 = createEntry(20L, firstDay, 120L);

        // Day 2: 8h-Meeting = Full day
        CalendarEntry entry3 = createEntry(30L, firstDay.plusDays(1), 480L);

        Calendar calendar = new Calendar();
        calendar.setId(99L);
        calendar.setEntries(Set.of(entry1, entry2, entry3));

        Employee employee = new Employee();
        employee.setId(employeeId);
        employee.setFirstName("Max");
        employee.setLastName("Mustermann");
        employee.setWorkingHoursPerDay(employeeWorkingHoursPerDay);
        employee.setCalendar(calendar);

        // WHEN
        Mockito.when(employeeService.getEmployeeById(employeeId)).thenReturn(employee);
        Mockito.when(calendarService.getCalendarOfEmployee(employeeId, firstDay, lastDayToCheck))
                .thenReturn(calendar);

        List<CalculatedCapacityCalendarEntryVO> result =
                resourceCapacityService.calculateFreeCapacity(task, employeeId, firstDay, lastDayToCheck);

        // THEN
        // Expected: Task should fit in first day (4h are free)
        CalculatedCapacityCalendarEntryVO expectedEntry = new CalculatedCapacityCalendarEntryVO(
                task.getTitle(),
                firstDay,
                task.getEstimatedTime()
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
        Task task = createTask(
                10L,
                "Customizing the Software",
                estimatedTaskDuration,
                LocalDate.parse("2025-11-05")
        );

        // Already given entries:
        // Day 1: one meeting 6h: 2h free
        CalendarEntry entry1 = createEntry(10L, firstDay, 6 * 60L);

        // Day 2: one meeting 6h: 2h free
        CalendarEntry entry2 = createEntry(20L, firstDay.plusDays(1), 6 * 60L);

        // Day 3:one meeting 4h: 4h free
        CalendarEntry entry3 = createEntry(30L, firstDay.plusDays(2), 4 * 60L);

        Long taskOccupiedTimeFirstDay = 120L; //2h
        Long taskOccupiedTimeSecondDay = 120L; //2h
        Long taskOccupiedTimeThirdDay = 60L; //1h

        Calendar calendar = new Calendar();
        calendar.setId(99L);
        calendar.setEntries(Set.of(entry1, entry2, entry3));

        Employee employee = new Employee();
        employee.setId(employeeId);
        employee.setFirstName("Max");
        employee.setLastName("Mustermann");
        employee.setWorkingHoursPerDay(employeeWorkingHoursPerDay);
        employee.setCalendar(calendar);

        // WHEN
        Mockito.when(employeeService.getEmployeeById(employeeId)).thenReturn(employee);
        Mockito.when(calendarService.getCalendarOfEmployee(employeeId, firstDay, lastDayToCheck))
                .thenReturn(calendar);

        List<CalculatedCapacityCalendarEntryVO> result =
                resourceCapacityService.calculateFreeCapacity(task, employeeId, firstDay, lastDayToCheck);

        // THEN
        // Expected: Task should fit first day 2h and second day 2h
        CalculatedCapacityCalendarEntryVO expectedEntryFirstDay = new CalculatedCapacityCalendarEntryVO(
                task.getTitle(),
                firstDay,
                taskOccupiedTimeFirstDay
        );

        CalculatedCapacityCalendarEntryVO expectedEntrySecondDay = new CalculatedCapacityCalendarEntryVO(
                task.getTitle(),
                firstDay.plusDays(1),
                taskOccupiedTimeSecondDay
        );

        CalculatedCapacityCalendarEntryVO expectedEntryThirdDay = new CalculatedCapacityCalendarEntryVO(
                task.getTitle(),
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
        Task task = createTask(
                10L,
                "Customizing the Software",
                estimatedTaskDuration,
                dueDate
        );

        // Already given entries:
        // Day 1: One Meeting = 6h: 2h free
        CalendarEntry entry1 = createEntry(10L, firstDay, 6 * 60L);

        // Day 2: 8h-Meeting: 0h free
        CalendarEntry entry2 = createEntry(30L, firstDay.plusDays(1), 8 * 60L);

        //Day 3: 7h-Meeting: 1h free
        CalendarEntry entry3 = createEntry(30L, firstDay.plusDays(2), 7 * 60L);

        Calendar calendar = new Calendar();
        calendar.setId(99L);
        calendar.setEntries(Set.of(entry1, entry2, entry3));

        Employee employee = new Employee();
        employee.setId(employeeId);
        employee.setFirstName("Max");
        employee.setLastName("Mustermann");
        employee.setWorkingHoursPerDay(employeeWorkingHoursPerDay);
        employee.setCalendar(calendar);

        // WHEN
        Mockito.when(employeeService.getEmployeeById(employeeId)).thenReturn(employee);
        Mockito.when(calendarService.getCalendarOfEmployee(employeeId, firstDay, lastDayToCheck))
                .thenReturn(calendar);

        //Excepted: Throws Exception, because in the given range is no capacity for the task
        assertThrows(
                NoCapacityUntilDueDateException.class,
                () -> resourceCapacityService.calculateFreeCapacity(task, employeeId, firstDay, dueDate)
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
        Task task = createTask(
                10L,
                "Customizing the Software",
                estimatedTaskDuration,
                LocalDate.parse("2025-11-05")
        );

        // Already given entries:
        // Friday: two meetings 3h = 6h: 2h free
        CalendarEntry entry1 = createEntry(10L, friday, 180L);
        CalendarEntry entry2 = createEntry(20L, friday, 180L);

        // Monday: 4h-Meeting: 4h free
        CalendarEntry entry3 = createEntry(30L, monday, 240L);

        Long taskOccupiedTimeFriday = 120L; //2h
        Long taskOccupiedTimeMonday = 180L; //3h

        Calendar calendar = new Calendar();
        calendar.setId(99L);
        calendar.setEntries(Set.of(entry1, entry2, entry3));

        Employee employee = new Employee();
        employee.setId(employeeId);
        employee.setFirstName("Max");
        employee.setLastName("Mustermann");
        employee.setWorkingHoursPerDay(employeeWorkingHoursPerDay);
        employee.setCalendar(calendar);


        // WHEN
        Mockito.when(employeeService.getEmployeeById(employeeId)).thenReturn(employee);
        Mockito.when(calendarService.getCalendarOfEmployee(employeeId, friday, monday))
                .thenReturn(calendar);

        List<CalculatedCapacityCalendarEntryVO> result =
                resourceCapacityService.calculateFreeCapacity(task, employeeId, friday, monday);

        // THEN
        // Expected: Task should fit Friday 2h and Monday 2h
        CalculatedCapacityCalendarEntryVO expectedFriday = new CalculatedCapacityCalendarEntryVO(
                task.getTitle(),
                friday,
                taskOccupiedTimeFriday
        );

        CalculatedCapacityCalendarEntryVO expectedMonday = new CalculatedCapacityCalendarEntryVO(
                task.getTitle(),
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

        CalculatedCapacityCalendarEntryVO employee1entry1 = createCalculatedEntryDto(taskTitle, firstDay, 180L);
        CalculatedCapacityCalendarEntryVO employee1entry2 = createCalculatedEntryDto(taskTitle, secondDay, 180L);
        CalculatedCapacityCalendarEntryVO employee1entry3 = createCalculatedEntryDto(taskTitle, thirdDay, 240L);

        CalculatedCapacityCalendarEntryVO employee2entry1 = createCalculatedEntryDto(taskTitle, firstDay, 180L);
        CalculatedCapacityCalendarEntryVO employee2entry2 = createCalculatedEntryDto(taskTitle, secondDay, 120L);

        CalculatedCapacityCalendarEntryVO employee3entry1 = createCalculatedEntryDto(taskTitle, firstDay, 180L);
        CalculatedCapacityCalendarEntryVO employee3entry2 = createCalculatedEntryDto(taskTitle, secondDay, 180L); //needs longer than employee 2

        Employee employee1 = new Employee();
        employee1.setId(1L);
        employee1.setFirstName("Max");
        employee1.setLastName("Mustermann");
        employee1.setWorkingHoursPerDay(employeeWorkingHoursPerDay);

        Employee employee2 = new Employee();
        employee2.setId(2L);
        employee2.setFirstName("Sabine");
        employee2.setLastName("Mustermann");
        employee2.setWorkingHoursPerDay(employeeWorkingHoursPerDay);

        Employee employee3 = new Employee();
        employee3.setId(3L);
        employee3.setFirstName("Erich");
        employee3.setLastName("Mustermann");
        employee3.setWorkingHoursPerDay(employeeWorkingHoursPerDay);

        List<CalculatedCapacityCalendarEntryVO> employee1TaskEntries = List.of(employee1entry1, employee1entry2, employee1entry3);
        List<CalculatedCapacityCalendarEntryVO> employee2TaskEntries = List.of(employee2entry1, employee2entry2);
        List<CalculatedCapacityCalendarEntryVO> employee3TaskEntries = List.of(employee3entry1, employee3entry2);

        Map<Employee, List<CalculatedCapacityCalendarEntryVO>> employeeTaskEntries = Map.of(
                employee1, employee1TaskEntries,
                employee2, employee2TaskEntries,
                employee3, employee3TaskEntries);

        //WHEN
        List<Employee> result = resourceCapacityService.calculateEmployeesAbleToCompleteTaskEarliest(employeeTaskEntries);

        //THEN
        //Second employee should be chosen because he can finish the task earliest
        assertEquals(List.of(employee2), result);

    }
    @Test
    void calculateEmployeesAbleToCompleteEarliest_SeveralFits() {
        //GIVEN
        BigDecimal employeeWorkingHoursPerDay = BigDecimal.valueOf(8); // 8h working day
        String taskTitle = "Customizing the Software";

        LocalDate firstDay = LocalDate.parse("2025-09-08");
        LocalDate secondDay = firstDay.plusDays(1);
        LocalDate thirdDay = firstDay.plusDays(2);

        CalculatedCapacityCalendarEntryVO employee1entry1 = createCalculatedEntryDto(taskTitle, firstDay, 180L);
        CalculatedCapacityCalendarEntryVO employee1entry2 = createCalculatedEntryDto(taskTitle, secondDay, 180L);
        CalculatedCapacityCalendarEntryVO employee1entry3 = createCalculatedEntryDto(taskTitle, thirdDay, 240L);

        CalculatedCapacityCalendarEntryVO employee2entry1 = createCalculatedEntryDto(taskTitle, firstDay, 180L);
        CalculatedCapacityCalendarEntryVO employee2entry2 = createCalculatedEntryDto(taskTitle, secondDay, 180L);

        CalculatedCapacityCalendarEntryVO employee3entry1 = createCalculatedEntryDto(taskTitle, firstDay, 180L);
        CalculatedCapacityCalendarEntryVO employee3entry2 = createCalculatedEntryDto(taskTitle, secondDay, 180L); //needs same time as employee 1

        Employee employee1 = new Employee();
        employee1.setId(1L);
        employee1.setFirstName("Max");
        employee1.setLastName("Mustermann");
        employee1.setWorkingHoursPerDay(employeeWorkingHoursPerDay);

        Employee employee2 = new Employee();
        employee2.setId(2L);
        employee2.setFirstName("Sabine");
        employee2.setLastName("Mustermann");
        employee2.setWorkingHoursPerDay(employeeWorkingHoursPerDay);

        Employee employee3 = new Employee();
        employee3.setId(3L);
        employee3.setFirstName("Erich");
        employee3.setLastName("Mustermann");
        employee3.setWorkingHoursPerDay(employeeWorkingHoursPerDay);

        List<CalculatedCapacityCalendarEntryVO> employee1TaskEntries = List.of(employee1entry1, employee1entry2, employee1entry3);
        List<CalculatedCapacityCalendarEntryVO> employee2TaskEntries = List.of(employee2entry1, employee2entry2);
        List<CalculatedCapacityCalendarEntryVO> employee3TaskEntries = List.of(employee3entry1, employee3entry2);

        Map<Employee, List<CalculatedCapacityCalendarEntryVO>> employeeTaskEntries = Map.of(
                employee1, employee1TaskEntries,
                employee2, employee2TaskEntries,
                employee3, employee3TaskEntries);

        //WHEN
        List<Employee> result = resourceCapacityService.calculateEmployeesAbleToCompleteTaskEarliest(employeeTaskEntries);

        //THEN
        //Second and third employee should be chosen because he can finish the task earliest
        assertTrue(result.contains(employee2));
        assertTrue(result.contains(employee3));
        assertEquals(2, result.size());
    }

    private CalendarEntry createEntry(Long id, LocalDate date, Long duration) {
        CalendarEntry entry = new CalendarEntry();
        entry.setId(id);
        entry.setTitle("Meeting");
        entry.setDescription("Meeting with the team");
        entry.setDate(date);
        entry.setDuration(duration);

        return entry;
    }

    private CalculatedCapacityCalendarEntryVO createCalculatedEntryDto(String title, LocalDate date, Long duration) {
        return new CalculatedCapacityCalendarEntryVO(
                title,
                date,
                duration
        );
    }

    private CalculatedCapacityCalendarEntryVO createCalculatedCapacityEntryDto(String title, LocalDate date, Long duration) {
        return new CalculatedCapacityCalendarEntryVO(
                title,
                date,
                duration
        );
    }

    private Task createTask(Long id, String title, Long estimatedTime, LocalDate dueDate) {

        Expertise expertise = new Expertise();
        expertise.setId(1L);
        expertise.setName("Customizing");
        expertise.setDescription("");

        Task task = new Task();
        task.setId(id);
        task.setTitle(title);
        task.setCreationDate(dueDate.atStartOfDay());
        task.setEstimatedTime(estimatedTime);
        task.setWorkingTime(0L);
        task.setDueDate(dueDate);
        task.setPriority(Priority.MEDIUM);
        task.setCompetences(Set.of(expertise));

        return task;
    }

    private Task createTask(long id, String title, Long estimatedTime, LocalDate dueDate, Priority priority, Set<Competence> expertises) {

        Task task = new Task();
        task.setId(id);
        task.setTitle(title);
        task.setCreationDate(dueDate.atStartOfDay());
        task.setEstimatedTime(estimatedTime);
        task.setWorkingTime(0L);
        task.setDueDate(dueDate);
        task.setPriority(priority);
        task.setCompetences(expertises);

        return task;
    }

}
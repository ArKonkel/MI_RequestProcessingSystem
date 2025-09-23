package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.capacity;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.calendar.Calendar;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.calendar.CalendarEntry;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.calendar.CalendarService;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.employee.*;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.task.Task;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.task.TaskService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

@Service
@AllArgsConstructor
@Slf4j
public class CapacityServiceImpl implements CapacityService, TaskMatcher, CapacityCalculator {

    private final TaskService taskService;

    private final CalendarService calendarService;

    private final EmployeeService employeeService;

    private final CapacityMapper capacityMapper;

    @Override
    public MatchingEmployeeCapacitiesDto findBestMatchesForTask(Long taskId) {
        MatchingEmployeeCapacitiesVO matches = findBestMatches(taskId);

        return capacityMapper.toDto(matches);
    }

    /**
     * Finds the best matches for a given task.
     *
     * @param taskId of Task to find the best matches for
     * @return a MatchingEmployeeForTaskDto with the task and the best matching employees. Higher number is better match.
     * With the information which one can complete the task earliest.
     */
    @Override
    public MatchingEmployeeCapacitiesVO findBestMatches(Long taskId) {
        log.info("Finding best matches for task {}", taskId);
        Task task = taskService.getTaskById(taskId);

        checkIfTaskReadyForCapacityPlanning(task);

        List<CalculatedCapacitiesOfMatchVO> results = new ArrayList<>();

        // find best matching employees by expertise
        Map<Employee, Integer> competenceMatches = findBestMatchingEmployees(task);

        // calculate free capacity for each employee
        Map<Employee, List<CalculatedCapacityCalendarEntryVO>> capacitiesByEmployee = new HashMap<>();
        for (Employee employee : competenceMatches.keySet()) {
            List<CalculatedCapacityCalendarEntryVO> capacities = calculateFreeCapacity(
                    task,
                    employee.getId(),
                    LocalDate.now(),
                    task.getDueDate()
            );
            capacitiesByEmployee.put(employee, capacities);
        }

        // determine employees who can complete the task earliest
        List<Employee> earliestEmployees = calculateEmployeesAbleToCompleteTaskEarliest(capacitiesByEmployee);

        // build result list
        for (Employee employee : capacitiesByEmployee.keySet()) {
            CalculatedCapacitiesOfMatchVO result = new CalculatedCapacitiesOfMatchVO(
                    employee,
                    competenceMatches.get(employee).longValue(),
                    earliestEmployees.contains(employee),
                    capacitiesByEmployee.get(employee)
            );
            results.add(result);
        }

        return new MatchingEmployeeCapacitiesVO(task.getId(), results);
    }


    /**
     * Assigns a task to an employee. Creates calendar entries for the employee and the task.
     *
     * @param taskId        of task to assign to employee
     * @param selectedMatch to create entries from
     */
    @Override
    @Transactional
    public void assignMatchToEmployee(Long taskId, CalculatedCapacitiesOfMatchDto selectedMatch) {
        log.info("Assigning task {} to employee {}", taskId, selectedMatch.getEmployee().id());

        CalculatedCapacitiesOfMatchVO vo = capacityMapper.toVo(selectedMatch);

        taskService.assignTaskToUserOfEmployee(taskId, vo.employee().getId());
        calendarService.createCalendarEntriesForTask(taskId, vo.employee().getCalendar().getId(), vo.calculatedCalendarCapacities());
    }

    private void checkIfTaskReadyForCapacityPlanning(Task task) {
        List<String> errors = new ArrayList<>();

        if (task.getEstimatedTime().equals(BigDecimal.ZERO)) {
            errors.add("No estimated time set");
        }
        if (task.getDueDate().isBefore(LocalDate.now())) {
            errors.add("Due date is in the past");
        }
        if (task.getExpertise().isEmpty()) {
            errors.add("No expertise set");
        }

        if (!errors.isEmpty()) {
            throw new TaskNotReadyForResourcePlanningException(
                    "Task not ready for resource planning. " + String.join(", ", errors)
            );
        }
    }

    /**
     * Methode to calculate the next free slot for a given task and employee. Skips Weekends.
     *
     * @param task       the task to calculate the free capacity for
     * @param employeeId the employee to calculate the free capacity for
     * @param from       the start date of the calculation
     * @param to         the end date of the calculation
     * @return a list of free calendar entries for the given task and employee
     */
    public List<CalculatedCapacityCalendarEntryVO> calculateFreeCapacity(Task task, Long employeeId, LocalDate from, LocalDate to) {
        log.info("Calculating free capacity for task {} for employee {}", task.getId(), employeeId);

        Employee employee = employeeService.getEmployeeById(employeeId);
        BigDecimal dailyWorkingMinutes = employee.getWorkingHoursPerDay().multiply(BigDecimal.valueOf(60));

        Calendar calendar = calendarService.getCalendarOfEmployee(employeeId, from, to);
        Set<CalendarEntry> calendarEntries = calendar.getEntries();

        BigDecimal remainingTaskTime = task.getEstimatedTime();
        List<CalculatedCapacityCalendarEntryVO> calculatedSlots = new ArrayList<>();

        // Iterate over each day in the range
        for (LocalDate date = from; !date.isAfter(to); date = date.plusDays(1)) {
            BigDecimal occupiedMinutes = BigDecimal.ZERO;

            // Skip weekend days
            if (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
                continue;
            }

            /*
            BigDecimal calculation:
            res = bg1.compareTo(bg2);
                    if( res == 0 )
                 "Both values are equal"
              else if( res == 1 )
                 "First Value is greater "
              else if( res == -1 )
                 "Second value is greater"
             */

            // Only proceed if there is still task time left to schedule
            if (remainingTaskTime.compareTo(BigDecimal.ZERO) > 0) {
                // Calculate occupied time for this day
                for (CalendarEntry entry : calendarEntries) {
                    if (entry.getDate().equals(date)) {
                        occupiedMinutes = occupiedMinutes.add(BigDecimal.valueOf(entry.getDurationInMinutes()));
                    }
                }

                BigDecimal freeMinutes = dailyWorkingMinutes.subtract(occupiedMinutes);

                if (freeMinutes.compareTo(remainingTaskTime) > 0) {
                    freeMinutes = remainingTaskTime;
                }


                // Create new slot if there is free time
                if (freeMinutes.compareTo(BigDecimal.ZERO) > 0) {
                    CalculatedCapacityCalendarEntryVO newSlot = new CalculatedCapacityCalendarEntryVO(
                            task.getTitle(),
                            date,
                            freeMinutes.longValue()
                    );
                    calculatedSlots.add(newSlot);
                    remainingTaskTime = remainingTaskTime.subtract(freeMinutes);
                }
            } else {
                //don't check next days, when no task time left to schedule
                break;
            }
        }

        if (remainingTaskTime.compareTo(BigDecimal.ZERO) > 0) {
            throw new NoCapacityUntilDueDateException("No capacity for task " + task.getId() + " until due date");
        }

        return calculatedSlots;
    }


    public List<Employee> calculateEmployeesAbleToCompleteTaskEarliest(Map<Employee, List<CalculatedCapacityCalendarEntryVO>> employeeWithCalendarEntriesOfTask) {
        log.info("Calculating employees able to complete task earliest");

        Map<Employee, CalculatedCapacityCalendarEntryVO> latestEntriesOfEmployees = new HashMap<>();
        Map<Employee, CalculatedCapacityCalendarEntryVO> earliestEntries = new HashMap<>();
        List<Employee> earliestEmployees = new ArrayList<>();

        //Find first the latest entries for each List of entries of the task
        for (Employee employee : employeeWithCalendarEntriesOfTask.keySet()) {
            List<CalculatedCapacityCalendarEntryVO> entries = employeeWithCalendarEntriesOfTask.get(employee);

            CalculatedCapacityCalendarEntryVO latestEntry =
                    entries.stream().max(Comparator.comparing(CalculatedCapacityCalendarEntryVO::date)).orElseThrow();

            latestEntriesOfEmployees.put(employee, latestEntry);
        }

        //Second: Find min date of latestEntriesOfEmployees
        LocalDate earliestDate = latestEntriesOfEmployees.values().stream()
                .map(entry -> entry.date())
                .min((date1, date2) -> date1.compareTo(date2))
                .orElseThrow();

        for (Employee employee : latestEntriesOfEmployees.keySet()) {
            if (latestEntriesOfEmployees.get(employee).date().equals(earliestDate)) {

                earliestEntries.put(employee, latestEntriesOfEmployees.get(employee));
            }
        }

        // Third: Find the shortest entry (smallest durationInMinutes)
        long minDuration = earliestEntries.values().stream()
                .map(entry -> entry.durationInMinutes())
                .min((duration1, duration2) -> duration1.compareTo(duration2))
                .orElseThrow();

        for (Employee employee : earliestEntries.keySet()) {
            if (earliestEntries.get(employee).durationInMinutes() == minDuration) {
                earliestEmployees.add(employee);
            }
        }

        return earliestEmployees;
    }

    /**
     * Finds the best matching employees for a given task.
     *
     * @param task to find the best matching employees for
     * @return a Map with the employees and the best match defined by the number. Higher number is better match
     */
    public Map<Employee, Integer> findBestMatchingEmployees(Task task) {
        List<EmployeeExpertise> allEmployeeExpertises = employeeService.getAllEmployeeExpertises();
        Map<Employee, Integer> matchesByEmployee = new HashMap<>();

        // Sum points for each competence of the task
        for (EmployeeExpertise ee : allEmployeeExpertises) {
            if (!task.getExpertise().contains(ee.getExpertise())) {
                continue;
            }

            Employee employee = ee.getEmployee();
            int points = ee.getLevel().getPoints();

            if (matchesByEmployee.containsKey(employee)) {
                matchesByEmployee.put(employee, matchesByEmployee.get(employee) + points);
            } else {
                matchesByEmployee.put(employee, points);
            }
        }

        return matchesByEmployee;
    }
}

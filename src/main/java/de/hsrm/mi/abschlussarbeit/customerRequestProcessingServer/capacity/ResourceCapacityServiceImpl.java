package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.capacity;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.calendar.CalendarDto;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.calendar.CalendarEntryDto;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.calendar.CalendarModule;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.employee.EmployeeDto;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.employee.EmployeeExpertiseDto;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.employee.ExpertiseLevel;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.notification.InteractionManager;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.task.TaskDto;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.task.TaskManager;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.user.UserManager;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

@Service
@AllArgsConstructor
@Slf4j
public class ResourceCapacityServiceImpl implements ResourceCapacityService, TaskMatcher, CapacityCalculatorEngine {

    private final TaskManager taskManager;

    private final CalendarModule calendarModule;

    private final InteractionManager interactionManager;

    private final UserManager userManager;

    /**
     * Finds the best matches for a given task.
     *
     * @param taskId of Task to find the best matches for
     * @return a MatchingEmployeeForTaskDto with the task and the best matching employees. Higher number is better match.
     * With the information which one can complete the task earliest.
     */
    @Override
    public MatchingEmployeeForTaskDto findBestMatches(Long taskId) {
        log.info("Finding best matches for task {}", taskId);
        TaskDto task = taskManager.getTaskById(taskId);

        checkIfTaskReadyForResourcePlanning(task);

        List<MatchCalculationResultDto> results = new ArrayList<>();

        // find best matching employees by competences
        Map<EmployeeDto, Integer> competenceMatches = findBestMatchingEmployees(task);

        // calculate free capacity for each employee
        Map<EmployeeDto, List<CalculatedCapacityCalendarEntryDto>> capacitiesByEmployee = new HashMap<>();
        for (EmployeeDto employee : competenceMatches.keySet()) {
            List<CalculatedCapacityCalendarEntryDto> capacities = calculateFreeCapacity(
                    task,
                    employee.id(),
                    LocalDate.now(),
                    task.dueDate()
            );
            capacitiesByEmployee.put(employee, capacities);
        }

        // determine employees who can complete the task earliest
        List<EmployeeDto> earliestEmployees = calculateEmployeesAbleToCompleteTaskEarliest(capacitiesByEmployee);

        // build result list
        for (EmployeeDto employee : capacitiesByEmployee.keySet()) {
            MatchCalculationResultDto result = new MatchCalculationResultDto(
                    employee,
                    competenceMatches.get(employee).longValue(),
                    earliestEmployees.contains(employee),
                    capacitiesByEmployee.get(employee)
            );
            results.add(result);
        }

        return new MatchingEmployeeForTaskDto(task, results);
    }

    /**
     * Assigns a task to an employee. Creates calendar entries for the employee and the task.
     *
     * @param taskId        of task to assign to employee
     * @param selectedMatch to create entries from
     */
    @Override
    @Transactional
    public void assignMatchToEmployee(Long taskId, MatchCalculationResultDto selectedMatch) {
        log.info("Assigning task {} to employee {}", taskId, selectedMatch.employee().id());

        interactionManager.assignTaskToUserOfEmployee(taskId, selectedMatch.employee().id());
        calendarModule.createCalendarEntriesForTask(taskId, selectedMatch.employee().calendarId(), selectedMatch.calculatedCalendarCapacities());
    }

    private void checkIfTaskReadyForResourcePlanning(TaskDto task) {
        List<String> errors = new ArrayList<>();

        if (task.estimatedTime() == 0) {
            errors.add("No estimated time set");
        }
        if (task.dueDate().isBefore(LocalDate.now())) {
            errors.add("Due date is in the past");
        }
        if (task.competences().isEmpty()) {
            errors.add("No competences set");
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
     * @param taskDto    the task to calculate the free capacity for
     * @param employeeId the employee to calculate the free capacity for
     * @param from       the start date of the calculation
     * @param to         the end date of the calculation
     * @return a list of free calendar entries for the given task and employee
     */
    public List<CalculatedCapacityCalendarEntryDto> calculateFreeCapacity(TaskDto taskDto, Long employeeId, LocalDate from, LocalDate to) {
        log.info("Calculating free capacity for task {} for employee {}", taskDto.processItem().id(), employeeId);

        EmployeeDto employeeDto = userManager.getEmployeeById(employeeId);
        long dailyWorkingMinutes = employeeDto.workingHoursPerDay().longValue() * 60;

        CalendarDto calendarDto = calendarModule.getCalendarOfEmployee(employeeId, from, to);
        List<CalendarEntryDto> calendarEntryDtos = calendarDto.entries();

        Long remainingTaskTime = taskDto.estimatedTime();
        List<CalculatedCapacityCalendarEntryDto> calculatedSlots = new ArrayList<>();

        // Iterate over each day in the range
        for (LocalDate date = from; !date.isAfter(to); date = date.plusDays(1)) {
            Long occupiedMinutes = 0L;

            // Skip weekend days
            if (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
                continue;
            }

            // Only proceed if there is still task time left to schedule
            if (remainingTaskTime > 0) {
                // Calculate occupied time for this day
                for (CalendarEntryDto entry : calendarEntryDtos) {
                    if (entry.date().equals(date)) {
                        occupiedMinutes += entry.duration();
                    }
                }

                long freeMinutes = dailyWorkingMinutes - occupiedMinutes;

                if (freeMinutes > remainingTaskTime) {
                    freeMinutes = remainingTaskTime;
                }

                // Create new slot if there is free time
                if (freeMinutes > 0) {
                    CalculatedCapacityCalendarEntryDto newSlot = new CalculatedCapacityCalendarEntryDto(
                            taskDto.processItem().title(),
                            date,
                            freeMinutes
                    );
                    calculatedSlots.add(newSlot);
                    remainingTaskTime -= freeMinutes;
                }
            } else {
                //don't check next days, when no task time left to schedule
                break;
            }
        }

        if (remainingTaskTime > 0) {
            throw new NoCapacityUntilDueDateException("No capacity for task " + taskDto.processItem().id() + " until due date");
        }

        return calculatedSlots;
    }

    public List<EmployeeDto> calculateEmployeesAbleToCompleteTaskEarliest(Map<EmployeeDto, List<CalculatedCapacityCalendarEntryDto>> employeeWithCalendarEntriesOfTask) {
        log.info("Calculating employees able to complete task earliest");

        Map<EmployeeDto, CalculatedCapacityCalendarEntryDto> latestEntriesOfEmployees = new HashMap<>();
        Map<EmployeeDto, CalculatedCapacityCalendarEntryDto> earliestEntries = new HashMap<>();
        List<EmployeeDto> earliestEmployees = new ArrayList<>();

        //Find first the latest entries for each List of entries of the task
        for (EmployeeDto employee : employeeWithCalendarEntriesOfTask.keySet()) {
            List<CalculatedCapacityCalendarEntryDto> entries = employeeWithCalendarEntriesOfTask.get(employee);

            CalculatedCapacityCalendarEntryDto latestEntry =
                    entries.stream().max(Comparator.comparing(CalculatedCapacityCalendarEntryDto::date)).orElseThrow();

            latestEntriesOfEmployees.put(employee, latestEntry);
        }

        //Second: Find min date of latestEntriesOfEmployees
        LocalDate earliestDate = latestEntriesOfEmployees.values().stream()
                .map(entry -> entry.date())
                .min((date1, date2) -> date1.compareTo(date2))
                .orElseThrow();

        for (EmployeeDto employee : latestEntriesOfEmployees.keySet()) {
            if (latestEntriesOfEmployees.get(employee).date().equals(earliestDate)) {

                earliestEntries.put(employee, latestEntriesOfEmployees.get(employee));
            }
        }

        // Third: Find the shortest entry (smallest duration)
        long minDuration = earliestEntries.values().stream()
                .map(entry -> entry.duration())
                .min((duration1, duration2) -> duration1.compareTo(duration2))
                .orElseThrow();

        for (EmployeeDto employee : earliestEntries.keySet()) {
            if (earliestEntries.get(employee).duration() == minDuration) {
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
    public Map<EmployeeDto, Integer> findBestMatchingEmployees(TaskDto task) {
        List<EmployeeExpertiseDto> allEmployeeExpertises = userManager.getAllEmployeeExpertises();
        Map<Long, Integer> matchesById = new HashMap<>();
        Map<EmployeeDto, Integer> matchesByEmployee = new HashMap<>();

        for (EmployeeExpertiseDto employeeExpertise : allEmployeeExpertises) {
            if (!task.competences().contains(employeeExpertise.expertise())) {
                continue;
            }

            long employeeId = employeeExpertise.employeeId();
            ExpertiseLevel expertLevel = employeeExpertise.level();

            if (!matchesById.containsKey(employeeId)) {
                matchesById.put(employeeId, expertLevel.getPoints());
            } else {
                //replace key with new value
                matchesById.put(employeeId, matchesById.get(employeeId) + expertLevel.getPoints());
            }
        }

        List<Long> employeeIds = matchesById.keySet().stream().toList();
        List<EmployeeDto> employees = userManager.getEmployeesByIds(employeeIds);

        for (EmployeeDto employee : employees) {
            if (!matchesByEmployee.containsKey(employee)) {
                matchesByEmployee.put(employee, matchesById.get(employee.id()));
            }
        }

        return matchesByEmployee;
    }
}

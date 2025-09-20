package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.capacity;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.task.TaskDto;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.task.TaskManager;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.calendar.CalendarModule;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.notification.InteractionManager;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.employee.EmployeeDto;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
@Slf4j
public class ResourceCapacityServiceImpl implements ResourceCapacityService {

    private final CapacityCalculatorEngine capacityCalculatorEngine;

    private final TaskMatcher taskMatcher;

    private final TaskManager taskManager;

    private final CalendarModule calendarModule;

    private final InteractionManager interactionManager;

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
        Map<EmployeeDto, Integer> competenceMatches = taskMatcher.findBestMatchingEmployees(task);

        // calculate free capacity for each employee
        Map<EmployeeDto, List<CalculatedCapacityCalendarEntryDto>> capacitiesByEmployee = new HashMap<>();
        for (EmployeeDto employee : competenceMatches.keySet()) {
            List<CalculatedCapacityCalendarEntryDto> capacities = capacityCalculatorEngine.calculateFreeCapacity(
                    task,
                    employee.id(),
                    LocalDate.now(),
                    task.dueDate()
            );
            capacitiesByEmployee.put(employee, capacities);
        }

        // determine employees who can complete the task earliest
        List<EmployeeDto> earliestEmployees = capacityCalculatorEngine.calculateEmployeesAbleToCompleteTaskEarliest(capacitiesByEmployee);

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
     * @param taskId of task to assign to employee
     * @param selectedMatch to create entries from
     */
    @Override
    @Transactional
    public void assignTaskToEmployee(Long taskId, MatchCalculationResultDto selectedMatch) {
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

}

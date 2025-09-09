package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.resourceCapacityPlanner.service;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.resourceCapacityPlanner.dto.CalculatedCapacityCalendarEntryDto;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.resourceCapacityPlanner.dto.MatchCalculationResultDto;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.resourceCapacityPlanner.dto.MatchingEmployeeForTaskDto;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.resourceCapacityPlanner.exception.TaskNotReadyForResourcePlanningException;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.taskManager.dto.TaskDto;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.userManager.dto.EmployeeDto;
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

    /**
     * Finds the best matches for a given task.
     *
     * @param task to find the best matches for
     * @return a MatchingEmployeeForTaskDto with the task and the best matching employees. Higher number is better match.
     * With the information which one can complete the task earliest.
     */
    @Override
    public MatchingEmployeeForTaskDto findBestMatches(TaskDto task) {
        log.info("Finding best matches for task {}", task.processItem().id());

        checkIfTaskReadyForResourcePlanning(task);

        List<MatchCalculationResultDto> results = new ArrayList<>();

        // find best matching employees by competence
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

    private void checkIfTaskReadyForResourcePlanning(TaskDto task) {
        List<String> errors = new ArrayList<>();

        if (task.estimatedTime() == 0) {
            errors.add("No estimated time set");
        }
        if (task.dueDate().isBefore(LocalDate.now())) {
            errors.add("Due date is in the past");
        }
        if (task.competence().isEmpty()) {
            errors.add("No competence set");
        }

        if (!errors.isEmpty()) {
            throw new TaskNotReadyForResourcePlanningException(
                    "Task not ready for resource planning. " + String.join(", ", errors)
            );
        }
    }

}

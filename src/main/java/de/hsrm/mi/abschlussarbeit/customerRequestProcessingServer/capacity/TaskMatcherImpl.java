package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.capacity;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.task.TaskDto;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.employee.ExpertiseLevel;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.employee.EmployeeDto;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.employee.EmployeeExpertiseDto;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.user.UserManager;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
@Slf4j
public class TaskMatcherImpl implements TaskMatcher {

    private final UserManager userManager;

    /**
     * Finds the best matching employees for a given task.
     * @param task to find the best matching employees for
     * @return a Map with the employees and the best match defined by the number. Higher number is better match
     */
    @Override
    public Map<EmployeeDto, Integer> findBestMatchingEmployees(TaskDto task) {
        List<EmployeeExpertiseDto> allEmployeeExpertises = userManager.getAllEmployeeExpertises();
        Map<Long, Integer> matchesById = new HashMap<>();
        Map<EmployeeDto, Integer> matchesByEmployee = new HashMap<>();

        for (EmployeeExpertiseDto employeeExpertise : allEmployeeExpertises) {
            if(!task.competences().contains(employeeExpertise.expertise())){
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

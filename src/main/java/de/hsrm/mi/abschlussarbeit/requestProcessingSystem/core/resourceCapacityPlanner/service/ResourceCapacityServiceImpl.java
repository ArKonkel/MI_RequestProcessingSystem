package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.resourceCapacityPlanner.service;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.taskManager.dto.TaskDto;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.userManager.dto.EmployeeDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@AllArgsConstructor
@Slf4j
public class ResourceCapacityServiceImpl implements ResourceCapacityService {

    @Override
    public Map<EmployeeDto, Integer> findBestMatchingEmployeesWithCalendar(TaskDto task) {
        return Map.of();
    }
//Todo. Check if Task is ready to be capacity planned

}

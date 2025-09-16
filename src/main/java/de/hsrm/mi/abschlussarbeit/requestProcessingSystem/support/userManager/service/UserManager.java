package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.userManager.service;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.userManager.dto.EmployeeDto;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.userManager.dto.EmployeeExpertiseDto;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.userManager.dto.UserDto;

import java.util.List;

public interface UserManager {

    List<EmployeeExpertiseDto> getAllEmployeeExpertises();

    List<EmployeeDto> getEmployeesByIds(List<Long> ids);

    EmployeeDto getEmployeeById(Long employeeId);

    UserDto getUserOfEmployee(Long employeeId);
}

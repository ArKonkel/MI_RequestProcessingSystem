package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.userManager.service;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.userManager.dto.EmployeeDto;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.userManager.dto.EmployeeExpertiseDto;

import java.util.List;

public interface EmployeeService {

    List<EmployeeExpertiseDto> getAllEmployeeExpertises();

    List<EmployeeDto> getEmployeesByIds(List<Long> ids);
}

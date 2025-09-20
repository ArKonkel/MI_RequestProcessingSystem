package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.employee;

import java.util.List;

public interface EmployeeService {

    List<EmployeeExpertiseDto> getAllEmployeeExpertises();

    List<EmployeeDto> getEmployeesByIds(List<Long> ids);

    EmployeeDto getEmployeeById(Long employeeId);
}

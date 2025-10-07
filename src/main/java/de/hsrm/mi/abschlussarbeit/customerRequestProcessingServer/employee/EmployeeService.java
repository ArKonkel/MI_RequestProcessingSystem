package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.employee;

import java.util.List;

public interface EmployeeService {

    List<EmployeeExpertise> getAllEmployeeExpertises();

    List<EmployeeDto> getAllDtoEmployees();

    Employee getEmployeeById(Long employeeId);
}

package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.employee;

import java.util.List;

public interface EmployeeService {

    List<EmployeeExpertise> getAllEmployeeExpertises();

    List<Employee> getEmployeesByIds(List<Long> ids);

    Employee getEmployeeById(Long employeeId);
}

package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.employee;

import java.util.List;

public interface EmployeeService {

    List<EmployeeExpertise> getAllEmployeeExpertises();

    List<EmployeeDto> getAllDtoEmployees();

    void addEmployeeExpertise(Long employeeId, Long expertiseId, ExpertiseLevel level);

    Employee getEmployeeById(Long employeeId);
}

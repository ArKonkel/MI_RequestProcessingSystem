package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.employee;

import java.util.List;

public interface EmployeeService {

    List<EmployeeExpertise> getAllEmployeeExpertises();

    List<EmployeeDto> getAllDtoEmployees();

    void addEmployeeExpertise(Long employeeId, Long expertiseId, ExpertiseLevel level);

    EmployeeDto updateEmployee(Long employeeId, EmployeeUpdateDto employeeUpdateDto);

    Employee getEmployeeById(Long employeeId);
}

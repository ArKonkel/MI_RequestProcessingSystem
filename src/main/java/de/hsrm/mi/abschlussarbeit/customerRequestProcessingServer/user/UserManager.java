package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.user;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.employee.EmployeeDto;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.employee.EmployeeExpertiseDto;

import java.util.List;

public interface UserManager {

    List<EmployeeExpertiseDto> getAllEmployeeExpertises();

    List<EmployeeDto> getEmployeesByIds(List<Long> ids);

    EmployeeDto getEmployeeById(Long employeeId);

    UserDto getUserOfEmployee(Long employeeId);
}

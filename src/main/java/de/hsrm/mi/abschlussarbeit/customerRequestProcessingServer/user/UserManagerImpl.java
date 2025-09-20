package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.user;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.employee.EmployeeDto;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.employee.EmployeeExpertiseDto;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.employee.EmployeeService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class UserManagerImpl implements UserManager {

    private final EmployeeService employeeService;

    private final UserService userService;

    @Override
    public List<EmployeeExpertiseDto> getAllEmployeeExpertises() {
        return employeeService.getAllEmployeeExpertises();
    }

    @Override
    public List<EmployeeDto> getEmployeesByIds(List<Long> ids) {
        return employeeService.getEmployeesByIds(ids);
    }

    @Override
    public EmployeeDto getEmployeeById(Long employeeId) {
        return employeeService.getEmployeeById(employeeId);
    }

    @Override
    public UserDto getUserOfEmployee(Long employeeId) {
        return userService.getUserOfEmployee(employeeId);
    }
}

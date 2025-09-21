package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.employee;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class EmployeeServiceImpl implements EmployeeService{

    private final EmployeeExpertiseRepository employeeExpertiseRepository;

    private final EmployeeRepository employeeRepository;

    private final EmployeeMapper employeeMapper;

    private final EmployeeExpertiseMapper employeeExpertiseMapper;


    @Override
    public List<EmployeeExpertise> getAllEmployeeExpertises() {
        log.info("Getting all employee expertises");

        return employeeExpertiseRepository.findAll();
    }

    @Override
    public List<Employee> getEmployeesByIds(List<Long> ids) {
        log.info("Getting employees with ids {}", ids);

        return employeeRepository.findByIdIn(ids);
    }

    @Override
    public Employee getEmployeeById(Long employeeId) {
        log.info("Getting employee with id {}", employeeId);

        return employeeRepository.findById(employeeId).orElseThrow();
    }
}

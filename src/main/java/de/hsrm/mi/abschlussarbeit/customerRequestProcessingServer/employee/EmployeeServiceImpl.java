package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.employee;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.expertise.Expertise;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.expertise.ExpertiseService;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.globalExceptionHandler.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeExpertiseRepository employeeExpertiseRepository;

    private final EmployeeRepository employeeRepository;

    private final EmployeeMapper employeeMapper;

    private final ExpertiseService expertiseService;

    private final EmployeeExpertiseMapper employeeExpertiseMapper;


    @Override
    public List<EmployeeExpertise> getAllEmployeeExpertises() {
        log.info("Getting all employee expertises");

        return employeeExpertiseRepository.findAll();
    }

    @Override
    public List<EmployeeDto> getAllDtoEmployees() {
        log.info("Getting all dto employees");

        List<Employee> employees = employeeRepository.findAllByOrderByLastNameAscFirstNameAsc();

        return employees.stream().map(employeeMapper::toDto).toList();
    }

    @Override
    public void addEmployeeExpertise(Long employeeId, Long expertiseId, ExpertiseLevel level) {
        log.info("Adding employee expertise {} to employee {}", expertiseId, employeeId);

        boolean exists = employeeExpertiseRepository.existsByEmployeeIdAndExpertiseId(employeeId, expertiseId);

        if (exists) {
            throw new IllegalArgumentException("Diese Expertise ist bereits für den Mitarbeiter vorhanden.");
        }

        Employee employee = getEmployeeById(employeeId);
        Expertise expertise = expertiseService.getExpertiseById(expertiseId);

        EmployeeExpertise employeeExpertiseToCreate = new EmployeeExpertise();
        employeeExpertiseToCreate.setEmployee(employee);
        employeeExpertiseToCreate.setExpertise(expertise);
        employeeExpertiseToCreate.setLevel(level);

        employeeExpertiseRepository.save(employeeExpertiseToCreate);
    }

    @Override
    public EmployeeDto updateEmployee(Long employeeId, EmployeeUpdateDto employeeUpdateDto) {
        log.info("Updating employee {}", employeeId);

        Employee employee = getEmployeeById(employeeId);

        if (employeeUpdateDto.firstName() != null) {
            employee.setFirstName(employeeUpdateDto.firstName());
        }

        if (employeeUpdateDto.lastName() != null) {
            employee.setLastName(employeeUpdateDto.lastName());
        }

        if (employeeUpdateDto.email() != null) {
            employee.setEmail(employeeUpdateDto.email());
        }

        if (employeeUpdateDto.workingHoursPerDay() != null) {
            employee.setWorkingHoursPerDay(employeeUpdateDto.workingHoursPerDay());
        }

        Employee savedEmployee = employeeRepository.save(employee);

        return employeeMapper.toDto(savedEmployee);
    }

    @Override
    public Employee getEmployeeById(Long employeeId) {
        log.info("Getting employee with id {}", employeeId);

        return employeeRepository.findById(employeeId).orElseThrow(() -> new NotFoundException("Employee with id " + employeeId + " not found"));
    }
}

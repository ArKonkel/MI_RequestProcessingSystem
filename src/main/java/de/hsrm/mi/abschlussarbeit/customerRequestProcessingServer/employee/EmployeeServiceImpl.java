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

    /**
     * Retrieves a list of all employee expertises.
     *
     * @return a list of EmployeeExpertise objects representing all employee expertises
     */
    @Override
    public List<EmployeeExpertise> getAllEmployeeExpertises() {
        log.info("Getting all employee expertises");

        return employeeExpertiseRepository.findAll();
    }

    /**
     * Retrieves a list of all employees as Data Transfer Objects (DTOs),
     * sorted by last name in ascending order and then by first name in ascending order.
     *
     * @return a list of EmployeeDto objects representing all employees
     */
    @Override
    public List<EmployeeDto> getAllDtoEmployees() {
        log.info("Getting all dto employees");

        List<Employee> employees = employeeRepository.findAllByOrderByLastNameAscFirstNameAsc();

        return employees.stream().map(employeeMapper::toDto).toList();
    }

    /**
     * Adds a specific expertise to an employee with a defined expertise level.
     *
     * @param employeeId  the unique identifier of the employee to whom the expertise will be added
     * @param expertiseId the unique identifier
     */
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

    /**
     * Updates the details of an existing employee based on the provided update data.
     *
     * @param employeeId        the unique identifier of the employee to be updated
     * @param employeeUpdateDto the data containing fields to update for the employee
     * @return the updated employee details as a Data Transfer Object (DTO)
     */
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

    /**
     * Retrieves an employee by their unique identifier.
     *
     * @param employeeId the unique identifier of the employee to be retrieved
     * @return the employee associated with the given identifier
     * @throws NotFoundException if no employee with the given identifier exists
     */
    @Override
    public Employee getEmployeeById(Long employeeId) {
        log.info("Getting employee with id {}", employeeId);

        return employeeRepository.findById(employeeId).orElseThrow(() -> new NotFoundException("Employee with id " + employeeId + " not found"));
    }
}

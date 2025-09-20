package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.employee;

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
    public List<EmployeeExpertiseDto> getAllEmployeeExpertises() {
        return employeeExpertiseRepository.findAll().stream().map(employeeExpertiseMapper::toDto).toList();
    }

    @Override
    public List<EmployeeDto> getEmployeesByIds(List<Long> ids) {
        return employeeRepository.findByIdIn(ids).stream().map(employeeMapper::toDto).toList();
    }

    @Override
    public EmployeeDto getEmployeeById(Long employeeId) {
        return employeeRepository.findById(employeeId).map(employeeMapper::toDto).orElseThrow();
    }
}

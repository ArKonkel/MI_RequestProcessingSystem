package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.employee;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.expertise.Expertise;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.expertise.ExpertiseService;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.globalExceptionHandler.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock
    EmployeeExpertiseRepository employeeExpertiseRepository;

    @Mock
    EmployeeRepository employeeRepository;

    @Mock
    EmployeeMapper employeeMapper;

    @Mock
    ExpertiseService expertiseService;

    @InjectMocks
    EmployeeServiceImpl service;

    Employee employee;
    Expertise expertise;
    EmployeeUpdateDto updateDto;

    @BeforeEach
    void setup() {
        employee = new Employee();
        employee.setId(1L);
        employee.setFirstName("Max");
        employee.setLastName("Mustermann");
        employee.setEmail("Max.Mustermann@test.de");

        expertise = new Expertise();
        expertise.setId(10L);

        updateDto = new EmployeeUpdateDto("Max", "Mustermann", "Max.Mustermann@test.de", BigDecimal.valueOf(8));
    }

    @Test
    void addEmployeeExpertise_shouldSaveExpertiseForEmployee() {
        when(employeeExpertiseRepository.existsByEmployeeIdAndExpertiseId(employee.getId(), expertise.getId())).thenReturn(false);
        when(employeeRepository.findById(employee.getId())).thenReturn(Optional.of(employee));
        when(expertiseService.getExpertiseById(expertise.getId())).thenReturn(expertise);

        service.addEmployeeExpertise(employee.getId(), expertise.getId(), ExpertiseLevel.EXPERT);

        verify(employeeExpertiseRepository).save(argThat(employeeExpertise ->
                employeeExpertise.getEmployee() == employee &&
                        employeeExpertise.getExpertise() == expertise &&
                        employeeExpertise.getLevel() == ExpertiseLevel.EXPERT
        ));
    }

    @Test
    void addEmployeeExpertise_shouldThrowException_ifExpertiseAlreadyExists() {
        when(employeeExpertiseRepository.existsByEmployeeIdAndExpertiseId(employee.getId(), expertise.getId())).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> service.addEmployeeExpertise(employee.getId(), expertise.getId(), ExpertiseLevel.EXPERT));
    }

    @Test
    void updateEmployee_shouldUpdateFieldsAndReturnDto() {
        when(employeeRepository.findById(employee.getId())).thenReturn(Optional.of(employee));
        when(employeeRepository.save(employee)).thenReturn(employee);

        EmployeeDto dto = new EmployeeDto(
                employee.getId(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getEmail(),
                employee.getWorkingHoursPerDay(),
                Set.of(),
                null,
                null,
                null
        );

        when(employeeMapper.toDto(employee)).thenReturn(dto);

        EmployeeDto result = service.updateEmployee(employee.getId(), updateDto);

        assertThat(employee.getFirstName()).isEqualTo("Max");
        assertThat(employee.getLastName()).isEqualTo("Mustermann");
        assertThat(employee.getEmail()).isEqualTo("Max.Mustermann@test.de");
        assertThat(employee.getWorkingHoursPerDay()).isEqualTo(BigDecimal.valueOf(8));

        assertThat(result).isEqualTo(dto);
    }

    @Test
    void updateEmployee_shouldThrowNotFoundException_ifEmployeeDoesNotExist() {
        when(employeeRepository.findById(employee.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> service.updateEmployee(employee.getId(), updateDto));
    }
}

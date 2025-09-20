package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.employee;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.competence.CompetenceDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

public record EmployeeDto(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        LocalDate hireDate,
        BigDecimal workingHoursPerDay,
        Set<EmployeeExpertiseDto> employeeExpertise,
        Set<CompetenceDto> competences,
        Long departmentId,
        Long userId,
        Long calendarId
) {}
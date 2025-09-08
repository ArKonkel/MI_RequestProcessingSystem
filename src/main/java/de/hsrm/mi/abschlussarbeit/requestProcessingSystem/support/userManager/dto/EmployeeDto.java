package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.userManager.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

public record EmployeeDto(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        Date hireDate,
        Set<EmployeeExpertiseDto> employeeExpertise,
        Set<CompetenceDto> competences,
        Long departmentId,
        Long userId,
        Long calendarId
) {}
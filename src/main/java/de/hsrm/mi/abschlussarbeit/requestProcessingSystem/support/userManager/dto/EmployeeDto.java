package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.userManager.dto;

import java.util.Date;
import java.util.Set;

public record EmployeeDto(
        long id,
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
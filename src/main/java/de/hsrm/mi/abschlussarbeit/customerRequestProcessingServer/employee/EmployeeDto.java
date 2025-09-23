package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.employee;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.expertise.ExpertiseDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

public record EmployeeDto(
        Long id,
        String firstName,
        String lastName,
        String email,
        BigDecimal workingHoursPerDay,
        Set<EmployeeExpertiseDto> employeeExpertise,
        Long departmentId,
        Long userId,
        Long calendarId
) {}
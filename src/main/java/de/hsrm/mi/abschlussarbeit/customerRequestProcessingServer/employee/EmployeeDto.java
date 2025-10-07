package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.employee;

import java.math.BigDecimal;
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
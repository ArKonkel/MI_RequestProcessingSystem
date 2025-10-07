package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.employee;

import java.math.BigDecimal;

public record EmployeeUpdateDto(String firstName, String lastName, String email, BigDecimal workingHoursPerDay) {
}

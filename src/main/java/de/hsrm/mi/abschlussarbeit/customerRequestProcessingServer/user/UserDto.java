package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.user;

public record UserDto(
        Long id,
        String name,
        String description,
        Long employeeId
) {
}

package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.user;

public record UserDto(
        Long id,
        String name,
        String description,
        Long employeeId
) {
}

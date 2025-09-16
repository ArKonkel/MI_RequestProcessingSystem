package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.userManager.dto;

public record UserDto(
        Long id,
        String name,
        String description,
        Long employeeId
) {
}

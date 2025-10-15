package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.user;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.role.RoleDto;

import java.util.List;

public record UserDto(
        Long id,
        String name,
        List<RoleDto> roles
) {
}

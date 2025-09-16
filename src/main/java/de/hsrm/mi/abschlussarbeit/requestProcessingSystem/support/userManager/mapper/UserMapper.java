package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.userManager.mapper;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.userManager.dto.UserDto;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.userManager.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "employeeId", source = "employee.id")
    UserDto toDto(User user);

}

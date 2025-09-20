package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "employeeId", source = "employee.id")
    UserDto toDto(User user);

}

package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.user;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    //@Mapping(target = "employeeId", source = "employee.id")
    UserDto toDto(User user);

}

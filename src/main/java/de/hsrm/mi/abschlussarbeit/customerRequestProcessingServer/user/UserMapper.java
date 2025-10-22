package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.user;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.customer.CustomerMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = CustomerMapper.class)
public interface UserMapper {

    @Mapping(target = "employeeId", source = "employee.id")
    UserDto toDto(User user);

}

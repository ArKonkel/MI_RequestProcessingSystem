package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.user;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.customer.CustomerMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = CustomerMapper.class)
public interface UserMapper {

    //@Mapping(target = "employeeId", source = "employee.id")
    UserDto toDto(User user);

}

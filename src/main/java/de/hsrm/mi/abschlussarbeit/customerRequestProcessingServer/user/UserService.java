package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.user;

public interface UserService {

    UserDto getUserOfEmployee(Long employeeId);

    User getUserById(Long id);
}

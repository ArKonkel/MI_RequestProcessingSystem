package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.user;

public interface UserService {

    User getUserOfEmployee(Long employeeId);

    User getUserById(Long id);
}

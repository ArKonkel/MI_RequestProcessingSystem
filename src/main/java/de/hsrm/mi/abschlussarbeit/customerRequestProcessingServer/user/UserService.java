package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.user;

import java.util.List;

public interface UserService {

    List<UserDto> getAllUsers();

    User getUserById(Long id);
}

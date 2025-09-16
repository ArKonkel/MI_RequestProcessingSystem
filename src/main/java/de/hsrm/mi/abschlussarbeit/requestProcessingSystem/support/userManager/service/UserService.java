package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.userManager.service;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.userManager.dto.UserDto;

public interface UserService {

    UserDto getUserOfEmployee(Long employeeId);
}

package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.userManager.service;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.userManager.dto.UserDto;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.userManager.mapper.UserMapper;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.userManager.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    @Override
    public UserDto getUserOfEmployee(Long employeeId) {
        log.info("Get user of employee {}", employeeId);

        return userMapper.toDto(userRepository.findByEmployeeId(employeeId));
    }
}

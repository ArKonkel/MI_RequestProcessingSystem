package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.user;

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

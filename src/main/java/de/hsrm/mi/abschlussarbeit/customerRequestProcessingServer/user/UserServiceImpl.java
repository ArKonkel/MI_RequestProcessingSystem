package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.user;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.globalExceptionHandler.NotFoundException;
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

    @Override
    public User getUserById(Long id) {
        log.info("Get user with id {}", id);

        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("User with id " + id + " not found"));
    }
}

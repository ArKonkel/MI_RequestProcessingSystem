package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.user;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.globalExceptionHandler.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    @Override
    public List<UserDto> getAllUsers() {
        log.info("Get all users dto");

        return userRepository.findAll().stream().map(userMapper::toDto).toList();
    }

    @Override
    public User getUserById(Long id) {
        log.info("Get user with id {}", id);

        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("User with id " + id + " not found"));
    }
}

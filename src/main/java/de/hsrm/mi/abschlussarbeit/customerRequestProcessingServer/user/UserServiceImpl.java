package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.user;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.globalExceptionHandler.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {

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

    @Override
    public UserDto getUserByName(String name) {
        log.info("Get user with name {}", name);

        User user = userRepository.findByName(name).orElseThrow(() -> new NotFoundException("User with name " + name + " not found"));

        return userMapper.toDto(user);
    }

    @Override
    public void addUser(User user) {
        log.info("Add user {}", user);

        if (userRepository.existsByName(user.getName())) {
            throw new UserAlreadyExistsException("User with name " + user.getName() + " already exists");
        }

        userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with name " + username + " not found"));
        return new CustomUserDetails(user);
    }
}

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

    /**
     * Retrieves a list of all users who are associated with an employee.
     *
     * @return a list of User
     */
    @Override
    public List<UserDto> getAllUsers() {
        log.info("Get all users dto");

        return userRepository.findAllByEmployeeNotNull().stream().map(userMapper::toDto).toList();
    }

    /**
     * Retrieves a user by their unique identifier.
     *
     * @param id the unique identifier of the user to retrieve
     * @return the User associated with the given id
     * @throws NotFoundException if no user is found with the specified id
     */
    @Override
    public User getUserById(Long id) {
        log.info("Get user with id {}", id);

        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("User with id " + id + " not found"));
    }

    /**
     * Retrieves a user by their name.
     *
     * @param name the name of the user to retrieve
     * @return the UserDto representation of the user with the specified name
     * @throws NotFoundException if no user is found with the specified name
     */
    @Override
    public UserDto getUserByName(String name) {
        log.info("Get user with name {}", name);

        User user = userRepository.findByName(name).orElseThrow(() -> new NotFoundException("User with name " + name + " not found"));

        return userMapper.toDto(user);
    }

    /**
     * Adds a new user to the system.
     *
     * @param user the user to be added
     * @throws UserAlreadyExistsException if a user with the same name already exists in the system
     */
    @Override
    public void addUser(User user) {
        log.info("Add user {}", user);

        if (userRepository.existsByName(user.getName())) {
            throw new UserAlreadyExistsException("User with name " + user.getName() + " already exists");
        }

        userRepository.save(user);
    }

    /**
     * Needed for security.
     * Loads a user by their username from the user repository and converts it to a UserDetails object.
     *
     * @param username the username of the user to be loaded
     * @return a UserDetails object representing the loaded user
     * @throws UsernameNotFoundException if no user is found with the specified username
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with name " + username + " not found"));
        return new CustomUserDetails(user);
    }
}

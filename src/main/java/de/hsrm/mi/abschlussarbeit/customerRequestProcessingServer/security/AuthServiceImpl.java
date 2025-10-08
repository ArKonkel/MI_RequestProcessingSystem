package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.security;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.role.Role;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.role.RoleService;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.user.User;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.user.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;

    private final RoleService roleService;

    // private final PasswordEncoder passwordEncoder;

    @Override
    public void register(RegisterDto registerDto) {
        //TODO make custom role
        Role role = roleService.getRoleByName("ADMIN");

        var user = new User();
        user.setName(registerDto.getName());
//        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setPassword(registerDto.getPassword());
        user.getRoles().add(role);

        userService.addUser(user);
    }
}

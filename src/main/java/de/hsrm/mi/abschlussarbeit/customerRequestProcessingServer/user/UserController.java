package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        log.info("REST request to get all users");
        List<UserDto> users = userService.getAllUsers();

        return ResponseEntity.ok().body(users);
    }

    @GetMapping("/{name}")
    public ResponseEntity<UserDto> getUserByName(@PathVariable String name) {
        log.info("REST request to get user with name {}", name);

        UserDto userDto = userService.getUserByName(name);

        return ResponseEntity.ok().body(userDto);
    }
}

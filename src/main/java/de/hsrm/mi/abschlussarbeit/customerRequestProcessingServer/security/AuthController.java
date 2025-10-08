package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.security;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.AccessDeniedException;

@RestController
@Slf4j
@Profile("!dev")
@AllArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterDto dto) {
        log.info("REST request to register: {}", dto);
        authService.register(dto);

        return ResponseEntity.ok("registered");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDto dto) throws AccessDeniedException {
        log.info("REST request to login: {}", dto);

        String token = authService.login(dto);
        return ResponseEntity.ok().body(token);
    }
}
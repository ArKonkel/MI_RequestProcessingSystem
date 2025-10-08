package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.security;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.role.Role;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.role.RoleService;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.user.User;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final UserService userService;
    private final RoleService roleService;
    private final AuthenticationManager authenticationManager;

    private final String jwtSecret;
    private final int jwtExpirationSeconds;

    public AuthServiceImpl(
            UserService userService,
            RoleService roleService,
            AuthenticationManager authenticationManager,
            @Value("${app.jwtSecret}") String jwtSecret,
            @Value("${app.jwtExpirationSeconds}") int jwtExpirationSeconds
    ) {
        this.userService = userService;
        this.roleService = roleService;
        this.authenticationManager = authenticationManager;
        this.jwtSecret = jwtSecret;
        this.jwtExpirationSeconds = jwtExpirationSeconds;
    }

    @Override
    public void register(RegisterDto registerDto) {
        //TODO make custom role
        Role role = roleService.getRoleByName("ADMIN");

        var user = new User();
        user.setName(registerDto.getUsername());
//        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setPassword(registerDto.getPassword());
        user.getRoles().add(role);

        userService.addUser(user);
    }

    @Override
    public String login(LoginDto dto) throws AccessDeniedException {
        log.info("Logging in user {}", dto.getUsername());

        String jwtToken, au;

        try {
            var tok = new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword());
            var authentication = authenticationManager.authenticate(tok);

            if (!authentication.isAuthenticated()) {
                throw new AccessDeniedException("Login credentials wrong");
            }

            var authorities = authentication.getAuthorities().toArray();
            au = authorities[0].toString();

            jwtToken = generateToken(dto.getUsername(), au);

        } catch (JOSEException | AuthenticationException | AccessDeniedException exc) {
            throw new AccessDeniedException(exc.getMessage());
        }
        return jwtToken;
    }

    public String generateToken(String username, String role) throws JOSEException {
        var timeNow = LocalDateTime.now();
        var expirationTime = timeNow.plusSeconds(jwtExpirationSeconds);

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject("RequestProcessingServer-Token")
                .issueTime(Date.from(timeNow.toInstant(ZoneOffset.UTC)))
                .expirationTime(Date.from(expirationTime.toInstant(ZoneOffset.UTC)))
                .claim("role", "ROLE_" + role)
                .build();

        SignedJWT signedJWT = new SignedJWT(
                new JWSHeader(JWSAlgorithm.HS256), claimsSet);
        JWSSigner signer = new MACSigner(jwtSecret);
        signedJWT.sign(signer);

        return signedJWT.serialize();
    }
}

package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.security;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.customer.Customer;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.customer.CustomerService;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.role.Role;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.role.RoleService;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.user.User;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
@Profile("!dev")
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final UserService userService;
    private final RoleService roleService;
    private final AuthenticationManager authenticationManager;

    private final CustomerService customerService;

    private final String jwtSecret;
    private final int jwtExpirationSeconds;

    public AuthServiceImpl(
            UserService userService,
            RoleService roleService,
            AuthenticationManager authenticationManager,
            CustomerService customerService,
            @Value("${app.jwtSecret}") String jwtSecret,
            @Value("${app.jwtExpirationSeconds}") int jwtExpirationSeconds
    ) {
        this.userService = userService;
        this.roleService = roleService;
        this.authenticationManager = authenticationManager;
        this.customerService = customerService;
        this.jwtSecret = jwtSecret;
        this.jwtExpirationSeconds = jwtExpirationSeconds;
    }


    /**
     * Registers a new customer account based on the provided registration data.
     * This method assigns the "CUSTOMER" role to the new user, links the user to an
     * already existing customer in the system, and saves the user in the database.
     * An exception is thrown if the customer linked to the provided email does not exist.
     *
     * @param registerDto an object containing the registration details, including username, email, and password
     * @throws CustomerNotInSystemException if no customer is found in the system with the given email
     */
    @Override
    public void registerAsCustomer(RegisterDto registerDto) {
        log.info("Registering user {}", registerDto.getUsername());

        Role role = roleService.getRoleByName("CUSTOMER");

        Customer customer = customerService.getCustomerByEmail(registerDto.getEmail()).orElseThrow(
                () -> new CustomerNotInSystemException("Customer with email " + registerDto.getEmail() + " does not exist in the system"));


        var user = new User();
        user.setName(registerDto.getUsername());
//        user.setPassword(passwordEncoder.encode(req.getPassword()));

        //Decoder should be used. But because of test data {noop} is before it
        String unsecurePw = "{noop}" + registerDto.getPassword(); //NOT FOR Production
        user.setPassword(unsecurePw);
        user.getRoles().add(role);
        user.setCustomer(customer);

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

            log.info(Arrays.toString(authorities));

            jwtToken = generateToken(dto.getUsername(), Arrays.stream(authorities).map(Object::toString).toList());

        } catch (JOSEException | AuthenticationException | AccessDeniedException exc) {
            throw new AccessDeniedException(exc.getMessage());
        }
        return jwtToken;
    }

    /**
     * Generates a JSON Web Token (JWT) for the given username with specified roles.
     * The token includes claims for the username, roles, issue time, and expiration time.
     *
     * @param username the username for which the token is being generated
     * @param roles    the list of roles associated with the user
     * @return a signed JSON Web Token (JWT) as a string
     * @throws JOSEException if there is an error during token signing
     */
    private String generateToken(String username, List<String> roles) throws JOSEException {
        var timeNow = LocalDateTime.now();
        var expirationTime = timeNow.plusSeconds(jwtExpirationSeconds);

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(username)
                .issueTime(Date.from(timeNow.toInstant(ZoneOffset.UTC)))
                .expirationTime(Date.from(expirationTime.toInstant(ZoneOffset.UTC)))
                .claim("roles", roles)
                .build();

        SignedJWT signedJWT = new SignedJWT(
                new JWSHeader(JWSAlgorithm.HS256), claimsSet);
        JWSSigner signer = new MACSigner(jwtSecret);
        signedJWT.sign(signer);

        return signedJWT.serialize();
    }
}

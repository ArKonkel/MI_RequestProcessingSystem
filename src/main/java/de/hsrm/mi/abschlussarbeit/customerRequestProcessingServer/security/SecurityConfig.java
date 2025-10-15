package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.spec.SecretKeySpec;

@Configuration
@Profile("!dev")
@EnableMethodSecurity
public class SecurityConfig {

    @Value("${app.jwtSecret}")
    private String jwtSecret;

    /**
     * Configures and provides a bean for the {@link AuthenticationManager}, which is used
     * to manage and authenticate user login requests. The manager is retrieved based on
     * the given {@link AuthenticationConfiguration}, ensuring proper authentication setup.
     *
     * @param configuration the authentication configuration object, which contains settings
     *                      for building and retrieving the authentication manager
     * @return an instance of {@link AuthenticationManager} configured for user authentication
     * @throws Exception if an error occurs while retrieving the authentication manager
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    /**
     * Configures and provides a bean for decoding JSON Web Tokens (JWT) using a symmetric key
     * derived from the application's secret. This decoder is used for validating the authenticity
     * and integrity of incoming JWT tokens.
     *
     * @return a configured NimbusJwtDecoder instance to validate JWT tokens.
     */
    @Bean
    public NimbusJwtDecoder jwtDecoder() {
        byte[] keyBytes = jwtSecret.getBytes();
        SecretKeySpec key = new SecretKeySpec(keyBytes, "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(key).build();
    }

    /**
     * Configures and provides a bean for JwtAuthenticationConverter to convert JWT token claims
     * to application-specific granted authorities.
     * Sets the claim name for authorities as "roles" and removes any prefix from
     * the authority claim.
     *
     * @return a configured JwtAuthenticationConverter that processes JWT claims for authorities.
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthoritiesClaimName("roles");
        grantedAuthoritiesConverter.setAuthorityPrefix("");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }

    /**
     * Configures the application's HTTP security settings by defining the security filter chain.
     * This method disables CSRF protection and form login, sets the session policy to stateless,
     * configures OAuth2 resource server for JWT decoding and authentication, and specifies
     * authorization rules for different request patterns and roles.
     *
     * @param http the {@link HttpSecurity} object used to configure HTTP security settings
     * @return the configured {@link SecurityFilterChain} instance
     * @throws Exception if an error occurs during the configuration process
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt ->
                                jwt.decoder(jwtDecoder())
                                        .jwtAuthenticationConverter(jwtAuthenticationConverter())
                        )
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/stompbroker/**").permitAll() //for websockets
                        .requestMatchers("/requests/**").hasAnyRole("ADMIN", "CUSTOMER_REQUEST_REVISER", "PROJECT_PLANNER", "CAPACITY_PLANNER")
                        .requestMatchers("/tasks/**").hasAnyRole("ADMIN", "TASK_REVISER", "PROJECT_PLANNER", "CAPACITY_PLANNER")
                        .requestMatchers("/projects/**").hasAnyRole("ADMIN", "PROJECT_PLANNER")
                        .requestMatchers("/capacity/**").hasAnyRole("ADMIN", "CAPACITY_PLANNER")
                        .requestMatchers("/calendars/**").hasAnyRole("ADMIN", "PROJECT_PLANNER", "CAPACITY_PLANNER", "TASK_REVISER")
                        .requestMatchers("/employees/**").hasAnyRole("ADMIN", "TASK_REVISER", "CAPACITY_PLANNER")
                        .requestMatchers("/actuator/**").hasRole("ADMIN")
                        .requestMatchers("/processItems/**").authenticated()
                        .requestMatchers("/users/**").authenticated()
                        //.anyRequest().authenticated()
                        .anyRequest().denyAll()
                );
        return http.build();
    }
}
package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@Profile("!dev")
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/tasks/**").hasRole("USER")
                        //.requestMatchers(...).hasAnyRole("USER", "ADMIN")
                        //.requestMatchers(...).hasAuthority("USER")
                        //.requestMatchers(...).hasAnyAuthority("USER", "ADMIN")
                        /*
                        .requestMatchers(HttpMethod.GET, "/api/requests/**")
                        .hasAnyRole("CUSTOMER", "REQUEST_REVISOR", "TASK_REVISOR", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/requests/**")
                        .hasAnyRole("CUSTOMER", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/requests/**")
                        .hasAnyRole("REQUEST_REVISOR", "ADMIN")
                        .requestMatchers("/api/tasks/**")
                        .hasAnyRole("TASK_REVISOR", "ADMIN")
                         */
                        .anyRequest().authenticated()
                )
                .httpBasic(withDefaults()); // for testing

        return http.build();
    }
}
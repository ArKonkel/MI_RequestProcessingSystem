package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.security;


import java.nio.file.AccessDeniedException;

public interface AuthService {

    void register(RegisterDto registerDto);

    String login(LoginDto loginDto) throws AccessDeniedException;
}
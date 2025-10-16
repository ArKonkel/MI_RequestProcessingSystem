package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.security;

import lombok.Data;

@Data
public class RegisterDto {
    private String username;
    private String email;
    private String password;
}
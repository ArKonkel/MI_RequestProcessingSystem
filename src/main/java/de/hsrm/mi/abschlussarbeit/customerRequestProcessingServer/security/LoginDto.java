package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.security;

import lombok.Data;

@Data
public class LoginDto {
    private String username;
    private String password;
}
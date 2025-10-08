package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.security;

import lombok.Data;

@Data
public class RegisterDto {
    private String name;
    private String password;
}
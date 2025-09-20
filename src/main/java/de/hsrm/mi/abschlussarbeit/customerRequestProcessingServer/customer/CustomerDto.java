package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.customer;

public record CustomerDto(Long id, String firstName, String lastName, String email, String address) {
}

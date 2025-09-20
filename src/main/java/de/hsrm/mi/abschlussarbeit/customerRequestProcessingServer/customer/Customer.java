package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.customer;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.customerRequest.Request;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String address;

    @OneToMany(mappedBy = "customer")
    private Set<Request> requests;
}

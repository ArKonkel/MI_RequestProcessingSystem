package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.customer;

import java.util.Optional;

public interface CustomerService {

    Customer getCustomerById(Long id);

    Optional<Customer> getCustomerByEmail(String email);
}

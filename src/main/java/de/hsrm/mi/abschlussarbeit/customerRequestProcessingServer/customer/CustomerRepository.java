package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.customer;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}

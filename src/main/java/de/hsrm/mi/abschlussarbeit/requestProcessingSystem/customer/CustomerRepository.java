package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.customer;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}

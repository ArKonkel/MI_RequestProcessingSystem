package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.customerManager.repository;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.customerManager.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}

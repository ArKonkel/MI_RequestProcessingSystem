package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.customerRequest;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRequestRepository extends JpaRepository<CustomerRequest, Long> {
}

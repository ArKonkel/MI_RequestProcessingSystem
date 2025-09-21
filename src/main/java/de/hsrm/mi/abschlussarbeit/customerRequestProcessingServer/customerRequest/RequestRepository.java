package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.customerRequest;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestRepository extends JpaRepository<CustomerRequest, Long> {
}

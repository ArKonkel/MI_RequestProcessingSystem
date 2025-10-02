package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.customerRequest;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerRequestRepository extends JpaRepository<CustomerRequest, Long> {

    List<CustomerRequest> findByCustomerIdOrderByCreationDateDesc(Long customerId);

    List<CustomerRequest> findByStatus(CustomerRequestStatus status);

}

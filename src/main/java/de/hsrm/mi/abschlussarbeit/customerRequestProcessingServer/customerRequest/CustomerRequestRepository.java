package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.customerRequest;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerRequestRepository extends JpaRepository<CustomerRequest, Long> {

    List<CustomerRequest> findByCustomerId(Long customerId, Sort sort);

    List<CustomerRequest> findAllByOrderByCreationDateDescIdDesc();

}

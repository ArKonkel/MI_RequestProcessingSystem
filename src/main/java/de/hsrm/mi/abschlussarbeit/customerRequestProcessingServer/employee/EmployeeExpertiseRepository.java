package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.employee;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeExpertiseRepository extends JpaRepository<EmployeeExpertise, Long> {

    boolean existsByEmployeeIdAndExpertiseId(Long employeeId, Long expertiseId);
}

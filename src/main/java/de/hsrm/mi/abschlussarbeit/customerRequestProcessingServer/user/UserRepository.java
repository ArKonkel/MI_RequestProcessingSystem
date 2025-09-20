package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmployeeId(Long employeeId);
}

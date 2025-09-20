package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmployeeId(Long employeeId);
}

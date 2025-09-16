package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.userManager.repository;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.userManager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmployeeId(Long employeeId);
}

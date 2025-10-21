package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByName(String username);

    Boolean existsByName(String username);

    List<User> findAllByEmployeeNotNull();
}

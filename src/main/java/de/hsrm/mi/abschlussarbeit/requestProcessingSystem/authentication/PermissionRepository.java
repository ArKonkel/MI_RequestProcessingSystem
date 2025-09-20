package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.authentication;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
}

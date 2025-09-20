package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.authentication;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
}

package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.userManager.repository;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.userManager.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
}

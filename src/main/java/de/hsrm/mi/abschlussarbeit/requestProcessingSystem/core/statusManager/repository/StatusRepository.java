package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.statusManager.repository;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.statusManager.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatusRepository extends JpaRepository<Status, Long> {
}

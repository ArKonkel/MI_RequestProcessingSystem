package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.requestManager.repository;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.requestManager.entity.Request;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestRepository extends JpaRepository<Request, Long> {
}

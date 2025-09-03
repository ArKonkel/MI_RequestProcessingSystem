package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.projectPlanner.repository;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.projectPlanner.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Long, Project> {
}

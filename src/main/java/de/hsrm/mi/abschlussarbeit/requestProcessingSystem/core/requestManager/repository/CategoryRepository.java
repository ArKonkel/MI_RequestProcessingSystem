package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.requestManager.repository;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.requestManager.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}

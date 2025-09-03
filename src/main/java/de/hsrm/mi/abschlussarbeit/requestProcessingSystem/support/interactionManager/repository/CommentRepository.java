package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.interactionManager.repository;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.interactionManager.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}

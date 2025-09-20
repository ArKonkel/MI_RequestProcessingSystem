package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.processItem;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}

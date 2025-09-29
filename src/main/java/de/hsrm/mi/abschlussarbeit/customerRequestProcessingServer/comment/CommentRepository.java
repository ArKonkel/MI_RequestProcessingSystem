package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.comment;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}

package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.task;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
}

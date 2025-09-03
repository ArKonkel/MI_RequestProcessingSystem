package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.taskManager.repository;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.taskManager.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Long, Task> {
}

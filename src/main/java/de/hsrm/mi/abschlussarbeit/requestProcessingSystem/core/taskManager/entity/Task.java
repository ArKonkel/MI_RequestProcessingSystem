package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.taskManager.entity;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.entity.ProcessItem;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.enums.Priority;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.projectPlanner.entity.Project;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.requestManager.entity.Request;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.calendarModule.entity.CalendarEntry;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Task extends ProcessItem {

    private long estimatedTime;

    private Date dueDate;

    private Priority priority;

    @OneToOne(mappedBy = "task")
    private CalendarEntry calendarEntry;

    @ManyToOne
    @JoinColumn(name = "request_id")
    private Request request;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    /**
     * Validation is needed because a Task should only be belong to Project or a Request
     */
    @PrePersist
    @PreUpdate
    private void validateBelonging() {
        if ((request != null && project != null)) {
            throw new IllegalStateException("Task is only allowed to belong either to a Request OR a Project");
        }
    }
}

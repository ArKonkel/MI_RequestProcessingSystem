package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.taskManager.entity;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.entity.ProcessItem;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.enums.Priority;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.projectPlanner.entity.Project;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.requestManager.entity.Request;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.calendarModule.entity.CalendarEntry;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.userManager.entity.Competence;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.Set;

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

    @ManyToMany
    @JoinTable(
            name = "task_competence",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "competence_id")
    )
    private Set<Competence> competences;

    @ManyToOne
    @JoinColumn(name = "blocker_id")
    private Task blocker;

    @ManyToOne
    @JoinColumn(name = "blocked_id")
    private Task blocked;

    @ManyToOne
    @JoinColumn(name = "reference_id") //parent of subtasks
    private Task referenceTask;

    @OneToMany(mappedBy = "referenceTask")
    private Set<Task> referencedBy;

    @ManyToOne
    @JoinColumn(name = "request_id")
    private Request request;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    /**
     * Validation is needed because a Task should only belong to Project or a Request
     */
    @PrePersist
    @PreUpdate
    private void validateBelonging() {
        if ((request != null && project != null)) {
            throw new IllegalStateException("Task is only allowed to belong either to a Request OR a Project");
        }
    }
}

package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.task;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.customerRequest.CustomerRequest;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.expertise.Expertise;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.processItem.ProcessItem;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.shared.Priority;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.project.Project;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.calendar.CalendarEntry;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.shared.EstimationUnit;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Task extends ProcessItem {

    @PositiveOrZero
    private Long estimatedTime; //TODO BigDecimal

    private EstimationUnit estimationUnit;

    @PositiveOrZero
    private Long workingTimeInMinutes;

    @Future
    private LocalDate dueDate;

    private String acceptanceCriteria;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Priority priority;

    @NotNull
    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    @OneToOne(mappedBy = "task")
    private CalendarEntry calendarEntry;

    @ManyToMany
    @JoinTable(
            name = "task_expertise",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "expertise_id")
    )
    private Set<Expertise> expertise = Set.of();

    @ManyToOne(fetch = FetchType.LAZY) //Needed because of circular dependency
    @JoinColumn(name = "blocker_id")
    private Task blocker;

    @ManyToOne(fetch = FetchType.LAZY) //Needed because of circular dependency
    @JoinColumn(name = "blocked_id")
    private Task blocked;

    @ManyToOne(fetch = FetchType.LAZY) //Needed because of circular dependency
    @JoinColumn(name = "reference_id")
    private Task referenceTask;

    @OneToMany(mappedBy = "referenceTask", fetch = FetchType.LAZY) //Needed because of circular dependency
    private Set<Task> referencedBy;

    @ManyToOne
    @JoinColumn(name = "request_id")
    private CustomerRequest request;

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

package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.processItem;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.task.Task;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.Set;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class ProcessItem {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "process_item_seq")
    @SequenceGenerator(name = "process_item_seq", sequenceName = "process_item_seq", allocationSize = 1)
    private Long id;

    @NotBlank
    private String title;

    private String description;

    @Column(nullable = false, updatable = false)
    private Instant creationDate;

    @OneToMany(mappedBy = "processItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Comment> comments;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User assignee;

    @PrePersist
    private void onCreate() {
        if (creationDate == null) {
            creationDate = Instant.now();
        }

        if (this instanceof Task task) {
            //Task should only belong to Project or a Request
            if (task.getRequest() != null && task.getProject() != null) {
                throw new IllegalStateException("Task is only allowed to belong either to a Request OR a Project");
            }

            //Estimation time can only be saved with estimationUnit
            if (task.getEstimatedTime() != null && task.getEstimationUnit() == null) {
                throw new IllegalStateException("Estimated time can only be saved with estimationUnit");
            }
        }
    }
}

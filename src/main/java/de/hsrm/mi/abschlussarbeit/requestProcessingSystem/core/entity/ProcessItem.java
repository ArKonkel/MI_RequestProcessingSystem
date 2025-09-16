package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.entity;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.interactionManager.entity.Comment;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.userManager.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
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

    @NotNull
    private LocalDateTime creationDate;

    @OneToMany(mappedBy = "processItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Comment> comments;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User assignee;
}

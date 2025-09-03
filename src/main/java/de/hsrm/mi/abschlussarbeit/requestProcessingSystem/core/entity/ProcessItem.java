package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.entity;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.statusManager.entity.Status;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.interactionManager.entity.Comment;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.userManager.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.Set;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class ProcessItem {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    private String name;

    private String description;

    private Date creationDate;

    @OneToMany(mappedBy = "processItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Comment> comments;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User assignee;

    @ManyToOne
    @JoinColumn(name = "status_id")
    private Status status;
}

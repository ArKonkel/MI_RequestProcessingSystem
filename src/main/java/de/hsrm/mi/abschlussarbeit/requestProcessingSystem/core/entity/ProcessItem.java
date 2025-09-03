package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.entity;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.interactionManager.entity.Comment;
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
public abstract class ProcessItem {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    private String name;

    private String description;

    private Date creationDate;

    @OneToMany(mappedBy = "processItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Comment> comments;
}

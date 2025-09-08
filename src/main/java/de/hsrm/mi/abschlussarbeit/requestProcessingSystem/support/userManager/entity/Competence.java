package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.userManager.entity;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.taskManager.entity.Task;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class Competence {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "competence_seq")
    @SequenceGenerator(name = "competence_seq", sequenceName = "competence_seq", allocationSize = 1)
    private Long id;

    private String name;

    private String description;

    @ManyToMany(mappedBy = "competences")
    private Set<Employee> employees;

    @ManyToMany(mappedBy = "competences")
    private Set<Task> tasks;
}

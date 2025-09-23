package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.expertise;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.task.Task;
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
public class Expertise {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "expertise_seq")
    @SequenceGenerator(name = "expertise_seq", sequenceName = "expertise_seq", allocationSize = 1)
    private Long id;

    private String name;

    private String description;

    @ManyToMany(mappedBy = "expertise")
    private Set<Task> tasks;
}

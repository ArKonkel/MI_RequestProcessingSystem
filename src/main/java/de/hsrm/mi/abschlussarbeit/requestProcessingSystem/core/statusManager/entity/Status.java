package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.statusManager.entity;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.entity.ProcessItem;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.enums.StatusType;
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
public class Status {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    private StatusType type;

    @OneToMany(mappedBy = "status")
    private Set<ProcessItem> processItems;
}

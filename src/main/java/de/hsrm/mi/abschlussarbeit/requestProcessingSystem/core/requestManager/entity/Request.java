package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.requestManager.entity;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.entity.ProcessItem;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.enums.Category;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.enums.Priority;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.enums.RequestStatus;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.projectPlanner.entity.Project;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.taskManager.entity.Task;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.customerManager.entity.Customer;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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
public class Request extends ProcessItem {

    private Priority priority;

    private Long estimatedScope;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    private boolean chargeable;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Category category;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToMany(mappedBy = "request")
    private Set<Task> tasks;

    @OneToMany(mappedBy = "request")
    private Set<Project> projects;

}

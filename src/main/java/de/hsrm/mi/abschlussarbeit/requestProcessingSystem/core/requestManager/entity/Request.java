package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.requestManager.entity;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.entity.ProcessItem;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.enums.Priority;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.taskManager.entity.Task;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.customerManager.entity.Customer;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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

    private boolean chargeable;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToMany(mappedBy = "request")
    private Set<Task> tasks;

}

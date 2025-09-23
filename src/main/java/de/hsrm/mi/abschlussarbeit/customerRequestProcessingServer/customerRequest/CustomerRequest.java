package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.customerRequest;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.processItem.ProcessItem;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.shared.Priority;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.project.Project;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.shared.EstimationUnit;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.task.Task;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.customer.Customer;
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
public class CustomerRequest extends ProcessItem {

    @NotNull
    @Enumerated(EnumType.STRING)
    private Priority priority;

    private Long estimatedScope;//TODO to BigDecimal

    @Enumerated(EnumType.STRING)
    private EstimationUnit scopeUnit;

    @NotNull
    @Enumerated(EnumType.STRING)
    private CustomerRequestStatus status;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Chargeable chargeable;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Category category;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    @NotNull
    private Customer customer;

    @OneToMany(mappedBy = "request")
    private Set<Task> tasks;

    @OneToMany(mappedBy = "request")
    private Set<Project> projects;

}

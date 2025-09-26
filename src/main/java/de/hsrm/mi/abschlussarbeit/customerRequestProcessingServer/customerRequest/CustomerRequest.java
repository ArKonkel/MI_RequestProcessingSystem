package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.customerRequest;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.processItem.ProcessItem;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.shared.Priority;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.project.Project;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.shared.TimeUnit;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.task.Task;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.customer.Customer;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomerRequest extends ProcessItem {

    @NotNull
    @Enumerated(EnumType.STRING)
    private Priority priority = Priority.LOW;

    private BigDecimal estimatedScope = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    private TimeUnit scopeUnit = TimeUnit.HOUR;

    @NotNull
    @Enumerated(EnumType.STRING)
    private CustomerRequestStatus status = CustomerRequestStatus.RECEIVED;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Chargeable chargeable = Chargeable.NOT_DETERMINED;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Category category = Category.OTHER;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    @NotNull
    private Customer customer;

    @OneToMany(mappedBy = "request")
    private Set<Task> tasks;

    @OneToMany(mappedBy = "request")
    private Set<Project> projects;


    /**
     * Validation of CustomerRequest
     */
    @PrePersist
    @PreUpdate
    private void validate() {
        //Estimation scope can only be saved with estimationUnit
        if (estimatedScope != null && scopeUnit == null) {
            throw new IllegalStateException("estimatedScope can only be saved with scopeUnit");
        }
    }
}

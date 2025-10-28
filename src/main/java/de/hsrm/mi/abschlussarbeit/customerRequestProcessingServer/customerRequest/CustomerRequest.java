package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.customerRequest;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.customer.Customer;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.processItem.ProcessItem;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.project.Project;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.shared.Priority;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.shared.TimeUnit;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.task.Task;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomerRequest extends ProcessItem {
    private String contactFirstName;

    private String contactLastName;

    private String contactPhoneNumber;

    private String programNumber;

    private String module;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Priority priority = Priority.LOW;

    private BigDecimal estimatedScope = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    private TimeUnit scopeUnit = TimeUnit.HOUR;

    @Enumerated(EnumType.STRING)
    private IsProjectClassification classifiedAsProject = IsProjectClassification.NOT_DETERMINED;

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
    private Set<Task> tasks = new HashSet<>();

    @OneToMany(mappedBy = "request")
    private Set<Project> projects = new HashSet<>();


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

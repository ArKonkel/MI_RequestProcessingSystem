package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.employee;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.expertise.Expertise;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeExpertise {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "employee_expertise_seq")
    @SequenceGenerator(name = "employee_expertise_seq", sequenceName = "employee_expertise_seq", allocationSize = 1)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    @NotNull
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "expertise_id")
    @NotNull
    private Expertise expertise;

    @Enumerated(EnumType.STRING)
    @NotNull
    private ExpertiseLevel level;

}

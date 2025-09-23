package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.employee;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.calendar.Calendar;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.department.Department;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @Email
    @NotBlank
    private String email;

    @NotNull
    @Positive
    @Max(8)
    private BigDecimal workingHoursPerDay;

    @OneToMany(mappedBy = "employee")
    private Set<EmployeeExpertise> employeeExpertise;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    @OneToOne(mappedBy = "employee")
    private User user;

    @OneToOne
    @JoinColumn(name = "calendar_id")
    private Calendar calendar;

}

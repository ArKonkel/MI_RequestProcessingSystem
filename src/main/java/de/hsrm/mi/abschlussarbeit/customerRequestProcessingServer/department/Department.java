package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.department;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.employee.Employee;
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
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String name;

    private String description;

    @OneToMany(mappedBy = "department")
    private Set<Employee> employees;
}

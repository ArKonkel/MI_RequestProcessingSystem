package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.calendar;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.employee.Employee;
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
public class Calendar {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @OneToMany(mappedBy = "calendar", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CalendarEntry> entries;

    @OneToOne(mappedBy = "calendar")
    private Employee employee;
}

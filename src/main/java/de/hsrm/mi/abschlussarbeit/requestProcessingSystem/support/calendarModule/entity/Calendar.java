package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.calendarModule.entity;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.userManager.entity.Employee;
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
    private long id;

    @OneToMany(mappedBy = "calendar", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CalendarEntry> entries;

    @OneToOne(mappedBy = "calendar")
    private Employee employee;
}

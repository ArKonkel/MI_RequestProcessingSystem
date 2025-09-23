package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.calendar;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.task.Task;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CalendarEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "calendar_entry_seq")
    @SequenceGenerator(name = "calendar_entry_seq", sequenceName = "calendar_entry_seq", allocationSize = 1)
    private Long id;

    private String title;

    private String description;

    @NotNull
    private LocalDate date;

    @NotNull
    private Long durationInMinutes;

    @ManyToOne
    @JoinColumn(name = "calendar_id")
    private Calendar calendar;

    @OneToOne
    @JoinColumn(name = "task_id")
    private Task task;
}

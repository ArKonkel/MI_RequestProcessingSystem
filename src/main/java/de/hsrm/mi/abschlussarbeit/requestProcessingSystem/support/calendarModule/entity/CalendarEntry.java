package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.calendarModule.entity;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.taskManager.entity.Task;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CalendarEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    private String title;

    private String description;

    private LocalDateTime start;

    private LocalDateTime end;

    @ManyToOne
    @JoinColumn(name = "calendar_id")
    private Calendar calendar;

    @OneToOne
    @JoinColumn(name = "task_id")
    private Task task;
}

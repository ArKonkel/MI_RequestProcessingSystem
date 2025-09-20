package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.project;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.processItem.ProcessItem;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.customerRequest.Request;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.task.Task;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Project extends ProcessItem {

    @Enumerated(EnumType.STRING)
    private ProjectStatus status;

    private LocalDate startDate;

    private LocalDate endDate;

    @ManyToOne
    @JoinColumn(name = "request_id")
    private Request request;

    @OneToMany(mappedBy = "project")
    private Set<Task> tasks;

    @ManyToOne
    @JoinColumn(name = "blocker_id")
    private Project blocker;

    @ManyToOne
    @JoinColumn(name = "blocked_id")
    private Project blocked;

    @ManyToOne
    @JoinColumn(name = "parent_id") //parent of subprojects
    private Project parent;

    @OneToMany(mappedBy = "parent")
    private Set<Project> subProjects;
}

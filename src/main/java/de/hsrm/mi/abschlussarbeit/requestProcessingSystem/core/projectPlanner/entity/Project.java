package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.projectPlanner.entity;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.entity.ProcessItem;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.requestManager.entity.Request;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.taskManager.entity.Task;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Project extends ProcessItem {

    private Date startDate;

    private Date endDate;

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

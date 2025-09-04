package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.projectPlanner.entity;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.entity.ProcessItem;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.requestManager.entity.Request;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

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
}

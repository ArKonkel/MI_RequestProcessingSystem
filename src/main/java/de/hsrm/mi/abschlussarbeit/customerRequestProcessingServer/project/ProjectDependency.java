package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.project;

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
public class ProjectDependency {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "source_project_id")
    private Project sourceProject;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "target_project_id")
    private Project targetProject;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ProjectDependencyType type;
}

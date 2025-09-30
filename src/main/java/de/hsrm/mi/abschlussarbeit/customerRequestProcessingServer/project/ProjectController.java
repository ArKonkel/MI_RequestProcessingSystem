package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.project;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectDto> getProject(@PathVariable Long projectId) {
        log.info("REST request to get project {}", projectId);

        ProjectDto dto = projectService.getProjectDtoById(projectId);
        return ResponseEntity.ok().body(dto);
    }

    @PostMapping("/dependency")
    public ResponseEntity<Void> createProjectDependency(@Valid @RequestBody CreateDependencyDto dependency) {
        log.info("REST request to create a new dependency");

        ProjectDependency createdDependency = projectService.createProjectDependency(dependency.sourceProjectId(), dependency.targetProjectId(), dependency.type());

        URI location = URI.create("api/dependencies/" + createdDependency.getId());
        return ResponseEntity.created(location).build();
    }

}

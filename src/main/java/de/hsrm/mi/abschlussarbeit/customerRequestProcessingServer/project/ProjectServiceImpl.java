package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.project;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.globalExceptionHandler.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;

    @Override
    public boolean isProjectReadyForProcessing(Long projectId) {
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new NotFoundException("Project with id " + projectId + " not found"));

        return (project.getStatus().equals(ProjectStatus.READY) || project.getStatus().equals(ProjectStatus.IN_PROGRESS));
    }
}
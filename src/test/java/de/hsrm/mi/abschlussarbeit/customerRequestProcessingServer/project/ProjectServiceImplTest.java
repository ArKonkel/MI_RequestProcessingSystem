package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.project;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProjectServiceImpl projectService;

    private Project sourceProject;
    private Project targetProject;
    private ProjectDependency dependency;

    @BeforeEach
    void setUp() {
        sourceProject = new Project();
        sourceProject.setId(1L);

        targetProject = new Project();
        targetProject.setId(2L);

        dependency = new ProjectDependency();
        dependency.setSourceProject(sourceProject);
        dependency.setTargetProject(targetProject);
        targetProject.setIncomingDependencies(Set.of(dependency));
    }

    @Test
    void updateProjectStatus_allowsReady_whenDependencyFinished() {
        dependency.setType(ProjectDependencyType.FINISH_TO_START);
        sourceProject.setStatus(ProjectStatus.FINISHED);
        targetProject.setStatus(ProjectStatus.CREATED);

        when(projectRepository.findById(targetProject.getId())).thenReturn(Optional.of(targetProject));
        // when(projectRepository.save(any(Project.class))).thenReturn(targetProject);

        assertDoesNotThrow(() -> projectService.updateProjectStatus(targetProject.getId(), ProjectStatus.READY));
        assertEquals(ProjectStatus.READY, targetProject.getStatus());
        //verify(projectRepository).save(targetProject);
    }

    @Test
    void updateProjectStatus_blocksReady_whenDependencyNotFinished() {
        dependency.setType(ProjectDependencyType.FINISH_TO_START);
        sourceProject.setStatus(ProjectStatus.IN_PROGRESS); // Not finished, should block
        targetProject.setStatus(ProjectStatus.CREATED);

        when(projectRepository.findById(targetProject.getId())).thenReturn(Optional.of(targetProject));

        assertThrows(BlockedByDependencyException.class,
                () -> projectService.updateProjectStatus(targetProject.getId(), ProjectStatus.READY));
    }

    @Test
    void updateProjectStatus_allowsInProgress_whenReadyAndDependenciesOk() {
        dependency.setType(ProjectDependencyType.FINISH_TO_START);
        sourceProject.setStatus(ProjectStatus.FINISHED);
        targetProject.setStatus(ProjectStatus.READY);

        when(projectRepository.findById(targetProject.getId())).thenReturn(Optional.of(targetProject));
        // when(projectRepository.save(any(Project.class))).thenReturn(targetProject);

        assertDoesNotThrow(() -> projectService.updateProjectStatus(targetProject.getId(), ProjectStatus.IN_PROGRESS));
        assertEquals(ProjectStatus.IN_PROGRESS, targetProject.getStatus());
        //verify(projectRepository).save(targetProject);
    }

    @Test
    void updateProjectStatus_blocksInProgress_whenNotReady() {
        targetProject.setStatus(ProjectStatus.CREATED);
        when(projectRepository.findById(targetProject.getId())).thenReturn(Optional.of(targetProject));

        assertThrows(BlockedByDependencyException.class,
                () -> projectService.updateProjectStatus(targetProject.getId(), ProjectStatus.IN_PROGRESS));
    }

    @Test
    void updateProjectStatus_allowsFinished_whenFinishToFinishSourceFinished() {
        dependency.setType(ProjectDependencyType.FINISH_TO_FINISH);
        sourceProject.setStatus(ProjectStatus.FINISHED);
        targetProject.setStatus(ProjectStatus.IN_PROGRESS);

        when(projectRepository.findById(targetProject.getId())).thenReturn(Optional.of(targetProject));
        // when(projectRepository.save(any(Project.class))).thenReturn(targetProject);

        assertDoesNotThrow(() -> projectService.updateProjectStatus(targetProject.getId(), ProjectStatus.FINISHED));
        assertEquals(ProjectStatus.FINISHED, targetProject.getStatus());
        //verify(projectRepository).save(targetProject);
    }

    @Test
    void updateProjectStatus_blocksFinished_whenFinishToFinishSourceNotFinished() {
        dependency.setType(ProjectDependencyType.FINISH_TO_FINISH);
        sourceProject.setStatus(ProjectStatus.IN_PROGRESS); // Not finished source blocks finish
        targetProject.setStatus(ProjectStatus.IN_PROGRESS);

        when(projectRepository.findById(targetProject.getId())).thenReturn(Optional.of(targetProject));

        assertThrows(BlockedByDependencyException.class,
                () -> projectService.updateProjectStatus(targetProject.getId(), ProjectStatus.FINISHED));
    }
}

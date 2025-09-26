package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.task;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.customerRequest.CustomerRequest;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.customerRequest.CustomerRequestService;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.globalExceptionHandler.NotAllowedException;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.processItem.ProcessItemDto;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.processItem.UpdateProcessItemDto;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.project.Project;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.project.ProjectService;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.shared.Priority;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.shared.TimeUnit;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.user.User;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TaskServiceWithoutMapperTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserService userService;

    @Mock
    private TaskMapper taskMapper;

    @Mock
    private ProjectService projectService;

    @Mock
    private CustomerRequestService customerRequestService;

    @InjectMocks
    private TaskServiceImpl taskService;

    private Task existingTask;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        existingTask = new Task();
        existingTask.setId(1L);
        existingTask.setTitle("Old Title");
        existingTask.setDescription("Old Description");
        existingTask.setPriority(Priority.MEDIUM);
        existingTask.setStatus(TaskStatus.OPEN);
    }

    @Test
    void updateTask_shouldUpdateAllFields() {
        // GIVEN
        UpdateProcessItemDto processItemDto = UpdateProcessItemDto.builder()
                .title("New Title")
                .description("New Description")
                .assigneeId(42L)
                .build();

        UpdateTaskDto updateDto = UpdateTaskDto.builder()
                .processItem(processItemDto)
                .estimatedTime(BigDecimal.valueOf(8))
                .estimationUnit(TimeUnit.HOUR)
                .workingTimeInMinutes(480L)
                .dueDate(LocalDate.of(2025, 12, 31))
                .status(TaskStatus.IN_PROGRESS)
                .priority(Priority.HIGH)
                .acceptanceCriteria("Updated criteria")
                .build();

        User assignee = new User();
        assignee.setId(42L);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));
        when(userService.getUserById(42L)).thenReturn(assignee);


        TaskDto expectedDto = new TaskDto(
                new ProcessItemDto(1L, "New Title", "New Description", null, 42L),
                BigDecimal.valueOf(8),
                TimeUnit.HOUR,
                480L,
                LocalDate.of(2025, 12, 31),
                Priority.HIGH,
                "",
                TaskStatus.IN_PROGRESS,
                null,
                Set.of(),
                null,
                1L

        );
        when(taskMapper.toDto(existingTask)).thenReturn(expectedDto);

        // WHEN
        TaskDto result = taskService.updateTask(1L, updateDto);

        // THEN
        assertNotNull(result);
        assertEquals("New Title", existingTask.getTitle());
        assertEquals("New Description", existingTask.getDescription());
        assertEquals(assignee, existingTask.getAssignee());
        assertEquals(BigDecimal.valueOf(8), existingTask.getEstimatedTime());
        assertEquals(TimeUnit.HOUR, existingTask.getEstimationUnit());
        assertEquals(480L, existingTask.getWorkingTimeInMinutes());
        assertEquals(LocalDate.of(2025, 12, 31), existingTask.getDueDate());
        assertEquals(TaskStatus.IN_PROGRESS, existingTask.getStatus());
        assertEquals(Priority.HIGH, existingTask.getPriority());
        assertEquals("Updated criteria", existingTask.getAcceptanceCriteria());
        assertEquals(expectedDto, result);

        verify(taskRepository).findById(1L);
        verify(userService).getUserById(42L);
        verify(taskRepository).save(existingTask);
        verify(taskMapper).toDto(existingTask);
    }


    @Test
    void updateTask_shouldThrowNotAllowedException_whenCustomerRequestNotReady() {
        // GIVEN
        CustomerRequest customerRequest = new CustomerRequest();
        customerRequest.setId(1L);

        existingTask.setRequest(customerRequest);

        UpdateProcessItemDto processItemDto = UpdateProcessItemDto.builder()
                .title("New Title")
                .description("New Description")
                .assigneeId(42L)
                .build();

        UpdateTaskDto updateDto = UpdateTaskDto.builder()
                .processItem(processItemDto)
                .estimatedTime(BigDecimal.valueOf(8))
                .estimationUnit(TimeUnit.HOUR)
                .workingTimeInMinutes(480L)
                .dueDate(LocalDate.of(2025, 12, 31))
                .status(TaskStatus.IN_PROGRESS)
                .priority(Priority.HIGH)
                .acceptanceCriteria("Updated criteria")
                .build();

        User assignee = new User();
        assignee.setId(42L);

        // WHEN
        when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));
        when(customerRequestService.isRequestReadyForProcessing(existingTask.getRequest().getId())).thenReturn(false);

        // THEN

        assertThrows(NotAllowedException.class, () -> taskService.updateTask(1L, updateDto));
    }

    @Test
    void updateTask_shouldThrowNotAllowedException_whenProjectNotReady() {
        // GIVEN
        Project project = new Project();
        project.setId(1L);

        existingTask.setProject(project);

        UpdateProcessItemDto processItemDto = UpdateProcessItemDto.builder()
                .title("New Title")
                .description("New Description")
                .assigneeId(42L)
                .build();

        UpdateTaskDto updateDto = UpdateTaskDto.builder()
                .processItem(processItemDto)
                .estimatedTime(BigDecimal.valueOf(8))
                .estimationUnit(TimeUnit.HOUR)
                .workingTimeInMinutes(480L)
                .dueDate(LocalDate.of(2025, 12, 31))
                .status(TaskStatus.IN_PROGRESS)
                .priority(Priority.HIGH)
                .acceptanceCriteria("Updated criteria")
                .build();

        User assignee = new User();
        assignee.setId(42L);

        // WHEN
        when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));
        when(projectService.isProjectReadyForProcessing(existingTask.getProject().getId())).thenReturn(false);

        // THEN
        assertThrows(NotAllowedException.class, () -> taskService.updateTask(1L, updateDto));
    }
}

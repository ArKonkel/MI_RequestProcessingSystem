package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.task;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.calendar.CalendarEntry;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.customerRequest.CustomerRequest;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.project.Project;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.shared.Priority;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.shared.TimeUnit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

//Must be a SpringBootTest because it has dependencies to other mappers
@SpringBootTest
class TaskMapperTest {

    @Autowired
    private TaskMapper taskMapper;

    @Test
    void toDto() {
        // GIVEN
        CalendarEntry calendarEntry = new CalendarEntry();
        calendarEntry.setId(11L);

        CustomerRequest request = new CustomerRequest();
        request.setId(22L);

        Project project = new Project();
        project.setId(33L);

        // ProcessItem fields via an anonymous subclass of ProcessItem (abstract)
        Task task = new Task();
        task.setId(1L);
        task.setTitle("Task Title");
        task.setDescription("Task Description");
        task.setCreationDate(Instant.parse("2025-09-23T14:00:00Z"));

        task.setEstimatedTime(BigDecimal.valueOf(12.5));
        task.setEstimationUnit(TimeUnit.HOUR);
        task.setWorkingTimeInMinutes(120L);
        task.setDueDate(LocalDate.of(2025, 10, 10));
        task.setAcceptanceCriteria("Criteria");
        task.setPriority(Priority.HIGH);
        task.setStatus(TaskStatus.OPEN);

        task.setCalendarEntry(calendarEntry);
        task.setRequest(request);
        task.setProject(project);

        // WHEN
        TaskDto dto = taskMapper.toDto(task);

        // THEN
        assertNotNull(dto);
        assertNotNull(dto.processItem());
        assertEquals(1L, dto.processItem().id());
        assertEquals("Task Title", dto.processItem().title());
        assertEquals("Task Description", dto.processItem().description());
        assertEquals(Instant.parse("2025-09-23T14:00:00Z"), dto.processItem().creationDate());

        assertEquals(BigDecimal.valueOf(12.5), dto.estimatedTime());
        assertEquals(TimeUnit.HOUR, dto.estimationUnit());
        assertEquals(120L, dto.workingTimeInMinutes());
        assertEquals(LocalDate.of(2025, 10, 10), dto.dueDate());
        assertEquals("Criteria", dto.acceptanceCriteria());
        assertEquals(Priority.HIGH, dto.priority());
        assertEquals(TaskStatus.OPEN, dto.status());

        assertEquals(11L, dto.calendarEntryId());
        assertEquals(22L, dto.requestId());
        assertEquals(33L, dto.projectId());

        assertNotNull(dto.expertise());
        assertTrue(dto.expertise().isEmpty());
    }
}

package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.processItem;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.task.Task;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class ProcessItemMapperTest {

    @Autowired
    private ProcessItemMapper mapper;

    @Test
    void toDto() {
        // GIVEN
        User assignee = new User();
        assignee.setId(42L);

        //because ProcessItem is abstract
        ProcessItem processItem = new Task() {
        };
        processItem.setId(1L);
        processItem.setTitle("Test Title");
        processItem.setDescription("Test Description");
        processItem.setCreationDate(Instant.parse("2025-09-23T14:00:00Z"));
        processItem.setAssignee(assignee);

        // WHEN
        ProcessItemDto dto = mapper.toDto(processItem);

        // THEN
        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Test Title", dto.getTitle());
        assertEquals("Test Description", dto.getDescription());
        assertEquals(Instant.parse("2025-09-23T14:00:00Z"), dto.getCreationDate());
    }
}

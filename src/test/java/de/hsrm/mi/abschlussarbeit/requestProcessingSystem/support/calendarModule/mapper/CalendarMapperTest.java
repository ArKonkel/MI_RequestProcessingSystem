package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.calendarModule.mapper;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.calendarModule.dto.CalendarDto;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.calendarModule.dto.CalendarEntryDto;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.calendarModule.entity.Calendar;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.calendarModule.entity.CalendarEntry;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.userManager.entity.Employee;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class CalendarMapperTest {

    @Autowired
    private CalendarMapper calendarMapper;

    @Test
    void toDto() {
        // GIVEN
        Employee employee = new Employee();
        employee.setId(42L);

        CalendarEntry entry1 = new CalendarEntry();
        entry1.setId(1L);
        entry1.setTitle("Meeting");
        entry1.setDescription("Teammeeting");

        CalendarEntry entry2 = new CalendarEntry();
        entry2.setId(2L);
        entry2.setTitle("Workshop");
        entry2.setDescription("Java Workshop");

        Calendar calendar = new Calendar();
        calendar.setId(100L);
        calendar.setEmployee(employee);
        calendar.setEntries(Set.of(entry1, entry2));

        // WHEN
        CalendarDto dto = calendarMapper.toDto(calendar);

        // THEN
        assertNotNull(dto);
        assertEquals(100L, dto.id());
        assertEquals(42L, dto.employeeId());

        assertNotNull(dto.entries());
        assertEquals(2, dto.entries().size());

        // Check entries
        CalendarEntryDto mappedEntry1 = dto.entries().stream()
                .filter(e -> e.id() == 1L)
                .findFirst()
                .orElseThrow();
        assertEquals("Meeting", mappedEntry1.title());
        assertEquals("Teammeeting", mappedEntry1.description());

        CalendarEntryDto mappedEntry2 = dto.entries().stream()
                .filter(e -> e.id() == 2L)
                .findFirst()
                .orElseThrow();
        assertEquals("Workshop", mappedEntry2.title());
        assertEquals("Java Workshop", mappedEntry2.description());
    }
}

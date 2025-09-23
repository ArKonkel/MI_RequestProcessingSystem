package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.calendar;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class CalendarEntryMapperTest {
    private final CalendarEntryMapper calendarEntryMapper = Mappers.getMapper(CalendarEntryMapper.class);

    @Test
    void toDto() {
        // GIVEN
        CalendarEntry entry = new CalendarEntry();
        entry.setId(1L);
        entry.setTitle("Team Meeting");
        entry.setDescription("Besprechung aller Tasks");
        entry.setDate(LocalDate.parse("2025-09-05"));
        entry.setDurationInMinutes(120L);

        // WHEN
        CalendarEntryDto dto = calendarEntryMapper.toDto(entry);

        // THEN
        assertNotNull(dto);
        assertEquals(1L, dto.id());
        assertEquals("Team Meeting", dto.title());
        assertEquals("Besprechung aller Tasks", dto.description());
        assertEquals(LocalDate.parse("2025-09-05"), dto.date());
        assertEquals(120L, dto.durationInMinutes());
    }
}
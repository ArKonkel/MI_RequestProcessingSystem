package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.calendar;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.capacity.CalculatedCapacityCalendarEntryDto;

import java.time.LocalDate;
import java.util.List;

public interface CalendarModule {

    CalendarDto getCalendarOfEmployee(Long employeeId, LocalDate from, LocalDate to);

    CalendarDto createCalendarEntriesForTask(Long taskId,Long calendarId, List<CalculatedCapacityCalendarEntryDto> calendarEntries);
}

package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.calendarModule.service;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.resourceCapacityPlanner.dto.CalculatedCapacityCalendarEntryDto;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.calendarModule.dto.CalendarDto;

import java.time.LocalDate;
import java.util.List;

public interface CalendarService {

    CalendarDto getCalendarOfEmployee(Long employeeId, LocalDate from, LocalDate to);

    CalendarDto createCalendarEntriesForTask(Long taskId,Long calendarId, List<CalculatedCapacityCalendarEntryDto> calendarEntries);
}

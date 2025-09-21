package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.calendar;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.capacity.CalculatedCapacityCalendarEntryVO;

import java.time.LocalDate;
import java.util.List;

public interface CalendarService {

    CalendarDto getCalendarDtoOfEmployee(Long employeeId, LocalDate from, LocalDate to);

    Calendar getCalendarOfEmployee(Long employeeId, LocalDate from, LocalDate to);

    CalendarDto createCalendarEntriesForTask(Long taskId,Long calendarId, List<CalculatedCapacityCalendarEntryVO> calendarEntries);
}

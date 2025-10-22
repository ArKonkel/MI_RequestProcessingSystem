package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.calendar;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.capacity.CalculatedCapacityCalendarEntryVO;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;

public interface CalendarService {

    CalendarDto getCalendarDtoOfEmployee(Long employeeId, LocalDate from, LocalDate to);

    List<CalendarDto> getAllCalendars(LocalDate from, LocalDate to);

    Calendar getCalendarOfEmployee(Long employeeId, LocalDate from, LocalDate to);

    void createCalendarEntriesForTask(Long taskId,Long calendarId, List<CalculatedCapacityCalendarEntryVO> calendarEntries);

    void initCalendarOfEmployeeOfYear(Long employeeId, Year year);

    void removeCalendarEntriesOfTask(Long taskId);
}

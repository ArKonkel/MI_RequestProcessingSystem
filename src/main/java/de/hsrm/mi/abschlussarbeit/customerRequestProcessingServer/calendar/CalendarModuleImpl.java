package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.calendar;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.capacity.CalculatedCapacityCalendarEntryDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class CalendarModuleImpl implements CalendarModule {

    private CalendarService calendarService;

    @Override
    public CalendarDto getCalendarOfEmployee(Long employeeId, LocalDate from, LocalDate to) {
        return calendarService.getCalendarOfEmployee(employeeId, from, to);
    }

    @Override
    public CalendarDto createCalendarEntriesForTask(Long taskId, Long calendarId, List<CalculatedCapacityCalendarEntryDto> calendarEntries) {
        return calendarService.createCalendarEntriesForTask(taskId, calendarId, calendarEntries);
    }
}

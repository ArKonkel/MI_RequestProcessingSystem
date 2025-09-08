package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.calendarModule.service;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.calendarModule.dto.CalendarDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@AllArgsConstructor
@Slf4j
public class CalendarModuleImpl implements CalendarModule {

    private CalendarService calendarService;

    @Override
    public CalendarDto getCalendarOfEmployee(Long employeeId, LocalDate from, LocalDate to) {
        return calendarService.getCalendarOfEmployee(employeeId, from, to);
    }
}

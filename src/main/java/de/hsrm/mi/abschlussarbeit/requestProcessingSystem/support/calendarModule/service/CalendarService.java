package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.calendarModule.service;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.calendarModule.dto.CalendarDto;

import java.time.LocalDate;

public interface CalendarService {

    CalendarDto getCalendarOfEmployee(Long employeeId, LocalDate from, LocalDate to);
}

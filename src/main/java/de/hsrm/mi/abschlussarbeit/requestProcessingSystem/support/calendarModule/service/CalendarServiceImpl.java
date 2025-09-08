package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.calendarModule.service;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.calendarModule.dto.CalendarDto;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.calendarModule.entity.Calendar;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.calendarModule.entity.CalendarEntry;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.calendarModule.mapper.CalendarMapper;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.calendarModule.repository.CalendarRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Service
@AllArgsConstructor
@Slf4j
public class CalendarServiceImpl implements CalendarService {

    private final CalendarRepository calendarRepository;

    private final CalendarMapper calendarMapper;

    @Override
    public CalendarDto getCalendarOfEmployee(Long employeeId, LocalDate from, LocalDate to) {

        Set<CalendarEntry> filteredCalendarEntries = new HashSet<>();

        Calendar calendar = calendarRepository.findByEmployeeId(employeeId).stream().findFirst().orElseThrow();
        calendar.getEntries().forEach(calendarEntry -> {
            if (calendarEntry.getDate().isAfter(from) && calendarEntry.getDate().isBefore(to)) {
                filteredCalendarEntries.add(calendarEntry);
            }
        });

        calendar.setEntries(filteredCalendarEntries) ;

        return calendarMapper.toDto(calendar);
    }
}

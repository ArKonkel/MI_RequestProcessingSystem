package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.calendarModule.service;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.resourceCapacityPlanner.dto.CalculatedCapacityCalendarEntryDto;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.taskManager.entity.Task;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.taskManager.repository.TaskRepository;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.calendarModule.dto.CalendarDto;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.calendarModule.entity.Calendar;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.calendarModule.entity.CalendarEntry;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.calendarModule.mapper.CalendarMapper;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.calendarModule.repository.CalendarEntryRepository;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.calendarModule.repository.CalendarRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
@Slf4j
public class CalendarServiceImpl implements CalendarService {

    private final CalendarRepository calendarRepository;

    private final CalendarEntryRepository calendarEntryRepository;

    private final TaskRepository taskRepository;

    private final CalendarMapper calendarMapper;

    @Override
    public CalendarDto getCalendarOfEmployee(Long employeeId, LocalDate from, LocalDate to) {

        Set<CalendarEntry> filteredCalendarEntries = new HashSet<>();

        Calendar calendar = calendarRepository.findByEmployeeId(employeeId).stream().findFirst().orElseThrow();
        calendar.getEntries().forEach(calendarEntry -> {
            if (!calendarEntry.getDate().isBefore(from) && !calendarEntry.getDate().isAfter(to)) {
                filteredCalendarEntries.add(calendarEntry);
            }
        });

        calendar.setEntries(filteredCalendarEntries) ;

        return calendarMapper.toDto(calendar);
    }

    @Override
    @Transactional
    public CalendarDto createCalendarEntriesForTask(
            Long taskId,
            Long calendarId,
            List<CalculatedCapacityCalendarEntryDto> calendarEntries
    ) {
        log.info("Creating calendar entries for task {} into calendar {}", taskId, calendarId);

        Calendar calendar = calendarRepository.getReferenceById(calendarId);
        Task task = taskRepository.getReferenceById(taskId);

        List<CalendarEntry> savedEntries = new ArrayList<>();

        for (CalculatedCapacityCalendarEntryDto dto : calendarEntries) {
            CalendarEntry entry = new CalendarEntry();
            entry.setTitle(dto.title());
            entry.setDate(dto.date());
            entry.setDuration(dto.duration());
            entry.setCalendar(calendar);
            entry.setTask(task);

            savedEntries.add(entry);
        }

        calendarEntryRepository.saveAll(savedEntries);

        return calendarMapper.toDto(calendar);
    }
}

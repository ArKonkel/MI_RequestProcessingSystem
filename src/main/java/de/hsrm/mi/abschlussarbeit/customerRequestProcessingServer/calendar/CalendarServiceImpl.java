package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.calendar;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.capacity.CalculatedCapacityCalendarEntryVO;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.task.Task;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.task.TaskService;
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

    private final TaskService taskService;

    private final CalendarMapper calendarMapper;

    @Override
    public CalendarDto getCalendarDtoOfEmployee(Long employeeId, LocalDate from, LocalDate to) {
        Set<CalendarEntry> filteredCalendarEntries = new HashSet<>();

        Calendar calendar = calendarRepository.findByOwnerId(employeeId).stream().findFirst().orElseThrow();
        calendar.getEntries().forEach(calendarEntry -> {
            if (!calendarEntry.getDate().isBefore(from) && !calendarEntry.getDate().isAfter(to)) {
                filteredCalendarEntries.add(calendarEntry);
            }
        });

        calendar.setEntries(filteredCalendarEntries) ;

        return calendarMapper.toDto(calendar);
    }

    @Override
    public Calendar getCalendarOfEmployee(Long employeeId, LocalDate from, LocalDate to) {
        Set<CalendarEntry> filteredCalendarEntries = new HashSet<>();

        Calendar calendar = calendarRepository.findByOwnerId(employeeId).stream().findFirst().orElseThrow();
        calendar.getEntries().forEach(calendarEntry -> {
            if (!calendarEntry.getDate().isBefore(from) && !calendarEntry.getDate().isAfter(to)) {
                filteredCalendarEntries.add(calendarEntry);
            }
        });

        calendar.setEntries(filteredCalendarEntries) ;

        return calendar;
    }

    @Override
    @Transactional
    public void createCalendarEntriesForTask(
            Long taskId,
            Long calendarId,
            List<CalculatedCapacityCalendarEntryVO> calendarEntries
    ) {
        log.info("Creating calendar entries for task {} into calendar {}", taskId, calendarId);

        Calendar calendar = calendarRepository.getReferenceById(calendarId);
        Task task = taskService.getTaskById(taskId);

        List<CalendarEntry> savedEntries = new ArrayList<>();

        for (CalculatedCapacityCalendarEntryVO dto : calendarEntries) {
            CalendarEntry entry = new CalendarEntry();
            entry.setTitle(dto.title());
            entry.setDate(dto.date());
            entry.setDurationInMinutes(dto.durationInMinutes());
            entry.setCalendar(calendar);
            entry.setTask(task);

            savedEntries.add(entry);
        }

        calendarEntryRepository.saveAll(savedEntries);
    }
}

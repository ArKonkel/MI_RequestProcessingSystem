package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.calendar;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.capacity.CalculatedCapacityCalendarEntryVO;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.integration.outlook.OutlookCalendarService;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.integration.outlook.graphTypes.OutlookCalendarEvent;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.integration.outlook.graphTypes.OutlookCalendarViewResponse;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.shared.TimeUnit;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.shared.ToMinutesCalculator;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.task.Task;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.task.TaskService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@AllArgsConstructor
@Slf4j
public class CalendarServiceImpl implements CalendarService {

    private final CalendarRepository calendarRepository;

    private final CalendarEntryRepository calendarEntryRepository;

    private final CalendarMapper calendarMapper;

    private final TaskService taskService;

    private final OutlookCalendarService outlookCalendarService;

    @Override
    public CalendarDto getCalendarDtoOfEmployee(Long employeeId, LocalDate from, LocalDate to) {
        Set<CalendarEntry> filteredCalendarEntries = new HashSet<>();

        Calendar calendar = calendarRepository.findByOwnerId(employeeId);
        if (calendar == null) {
            throw new NoSuchElementException("Calendar not found for employee " + employeeId);
        }

        calendar.getEntries().forEach(calendarEntry -> {
            if (!calendarEntry.getDate().isBefore(from) && !calendarEntry.getDate().isAfter(to)) {
                filteredCalendarEntries.add(calendarEntry);
            }
        });

        calendar.setEntries(filteredCalendarEntries);

        return calendarMapper.toDto(calendar);
    }

    @Override
    public Calendar getCalendarOfEmployee(Long employeeId, LocalDate from, LocalDate to) {
        Set<CalendarEntry> filteredCalendarEntries = new HashSet<>();

        Calendar calendar = calendarRepository.findByOwnerId(employeeId);
        if (calendar == null) {
            throw new NoSuchElementException("Calendar not found for employee " + employeeId);
        }

        calendar.getEntries().forEach(calendarEntry -> {
            if (!calendarEntry.getDate().isBefore(from) && !calendarEntry.getDate().isAfter(to)) {
                filteredCalendarEntries.add(calendarEntry);
            }
        });

        calendar.setEntries(filteredCalendarEntries);

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

    /**
     * Initializes the calendar of an employee for a given year.
     *
     * @param employeeId of the employee to initialize the calendar for.
     * @param year to initialize the calendar for.
     */
    @Override
    @Transactional
    public void initCalendarOfEmployeeOfYear(Long employeeId, Year year) {
        log.info("Initializing calendar of year {} of employee {}", year, employeeId);

        LocalDate startOfYear = year.atMonth(java.time.Month.JANUARY).atDay(1);
        LocalDate endOfYear = year.atMonth(java.time.Month.DECEMBER).atDay(31);

        Calendar calendar = getCalendarOfEmployee(employeeId, startOfYear, endOfYear);
        String employeeMail = calendar.getOwner().getEmail();

        //LocalDate to OffsetDateTime
        ZoneOffset startOffset = ZoneId.systemDefault().getRules().getOffset(startOfYear.atStartOfDay());
        ZoneOffset endOffset = ZoneId.systemDefault().getRules().getOffset(endOfYear.atStartOfDay());
        OffsetDateTime start = startOfYear.atStartOfDay().atOffset(startOffset);
        OffsetDateTime end = endOfYear.atStartOfDay().atOffset(endOffset);

        OutlookCalendarViewResponse outlookCalendarViewResponse =
                outlookCalendarService.fetchCalendarEvents(employeeMail, start, end);

        BigDecimal workingHours = calendar.getOwner().getWorkingHoursPerDay();
        List<CalendarEntry> calendarEntries = parseOutlookCalendarToEntries(outlookCalendarViewResponse, workingHours, calendar);

        calendarEntryRepository.saveAll(calendarEntries);
    }

    /**
     * Parses the outlook calendar events into calendar entries.
     *
     * @param outlookCalendarViewResponse to parse from
     * @param employeeWorkingHours to calculate the duration of the calendar entries.
     * @param calendar to save the calendar entries to.
     * @return a list of calendar entries with the parsed events.
     */
    private List<CalendarEntry> parseOutlookCalendarToEntries(OutlookCalendarViewResponse outlookCalendarViewResponse, BigDecimal employeeWorkingHours, Calendar calendar) {
        List<CalendarEntry> calendarEntries = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        //format of DateTimeTimeZone of event
        //"dateTime": "2025-09-25T11:00:00",
        //"timeZone": "Europe/Berlin"
        for (OutlookCalendarEvent event : outlookCalendarViewResponse.value()) {
            // Parse event datetime and timezone into OffsetDateTimes.
            OffsetDateTime odtEventStart = LocalDateTime
                    .parse(event.start().dateTime(), formatter)
                    .atZone(ZoneId.of(event.start().timeZone()))
                    .toOffsetDateTime();

            OffsetDateTime odtEventEnd = LocalDateTime
                    .parse(event.end().dateTime(), formatter)
                    .atZone(ZoneId.of(event.end().timeZone()))
                    .toOffsetDateTime();

            //Need only days to loop through.
            LocalDate dayStart = odtEventStart.toLocalDate();
            LocalDate dayEnd = odtEventEnd.toLocalDate();

            // Go through all days of the event
            for (LocalDate curDay = dayStart; !curDay.isAfter(dayEnd); curDay = curDay.plusDays(1)) {
                // When its the first day, start the event at the actual start time, else we start at the beginning of the day.
                OffsetDateTime dayPeriodStart;
                if (curDay.equals(dayStart)) {
                    dayPeriodStart = odtEventStart;
                } else {
                    dayPeriodStart = curDay.atStartOfDay().atOffset(odtEventStart.getOffset());
                }

                // When its the lst day we start at the actual end time, else we start at the end of the day.
                OffsetDateTime dayPeriodEnd;
                if (curDay.equals(dayEnd)) {
                    dayPeriodEnd = odtEventEnd;
                } else {
                    dayPeriodEnd = curDay.plusDays(1).atStartOfDay().atOffset(odtEventEnd.getOffset());
                }

                // Calculate the duration of the event in minutes and then into BigDecimal
                long durationMinutes = Duration.between(dayPeriodStart, dayPeriodEnd).toMinutes();
                BigDecimal durationHours = BigDecimal.valueOf(durationMinutes).divide(BigDecimal.valueOf(60), 2, BigDecimal.ROUND_HALF_UP);

                //If the duration is longer than the daily working hours, set the duration to the daily working hours. Else set it to the actual duration.
                BigDecimal entryHours;
                if (durationHours.compareTo(employeeWorkingHours) > 0) {
                    entryHours = employeeWorkingHours;
                } else {
                    entryHours = durationHours;
                }

                // Create the new entries with the calculated duration for the day.
                CalendarEntry entry = new CalendarEntry();
                entry.setCalendar(calendar);
                entry.setTitle(event.subject());
                entry.setDescription(event.body().content());
                entry.setDate(curDay);
                entry.setDurationInMinutes(ToMinutesCalculator.timeUnitToMinutes(entryHours, TimeUnit.HOUR));

                calendarEntries.add(entry);
            }
        }
        return calendarEntries;
    }
}

package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.calendar;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.capacity.CalculatedCapacityCalendarEntryVO;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.globalExceptionHandler.NotFoundException;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.integration.outlook.OutlookCalendarService;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.integration.outlook.graphTypes.OutlookCalendarEvent;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.integration.outlook.graphTypes.OutlookCalendarViewResponse;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.shared.TimeUnit;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.shared.TimeCalculatorHelper;
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
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class CalendarServiceImpl implements CalendarService {

    private final CalendarRepository calendarRepository;

    private final CalendarEntryRepository calendarEntryRepository;

    private final CalendarMapper calendarMapper;

    private final TaskService taskService;

    private final OutlookCalendarService outlookCalendarService;

    /**
     * Retrieves a CalendarDto object representing the calendar entries of a specific employee
     * within a specified date range.
     *
     * @param employeeId the unique identifier of the employee whose calendar is to be retrieved
     * @param from the start date of the date range (inclusive)
     * @param to the end date of the date range (inclusive)
     * @return a CalendarDto containing the filtered calendar entries of the specified employee within the given date range
     */
    @Override
    public CalendarDto getCalendarDtoOfEmployee(Long employeeId, LocalDate from, LocalDate to) {
        log.info("Getting calendar for employee {} from {} to {}", employeeId, from, to);

        List<CalendarEntry> filteredCalendarEntries = new ArrayList<>();

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

    /**
     * Retrieves all calendars within the specified date range. Calendars will only include
     * entries that fall between the given dates, inclusive.
     *
     * @param from the start date of the range (inclusive)
     * @param to the end date of the range (inclusive)
     * @return a list of CalendarDto objects containing filtered calendar entries
     *         within the specified date range
     */
    @Override
    public List<CalendarDto> getAllCalendars(LocalDate from, LocalDate to) {
        log.info("Getting all calendars for period {} to {}", from, to);

        List<Calendar> calendars = calendarRepository.findAll();

        calendars.forEach(calendar -> {
            List<CalendarEntry> filteredCalendarEntries = new ArrayList<>();

            calendar.getEntries().forEach(calendarEntry -> {
                if (!calendarEntry.getDate().isBefore(from) && !calendarEntry.getDate().isAfter(to)) {
                    filteredCalendarEntries.add(calendarEntry);
                }
            });

            calendar.setEntries(filteredCalendarEntries);
        });

        return calendars.stream()
                .map(calendarMapper::toDto)
                .toList();
    }


    /**
     * Retrieves the calendar of a specific employee filtered by the given date range.
     *
     * @param employeeId the unique identifier of the employee whose calendar is to be retrieved
     * @param from the start date of the range within which calendar entries should fall
     * @param to the end date of the range within which calendar entries should fall
     * @return the filtered calendar object containing entries within the specified date range
     */
    @Override
    public Calendar getCalendarOfEmployee(Long employeeId, LocalDate from, LocalDate to) {
        List<CalendarEntry> filteredCalendarEntries = new ArrayList<>();

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

    /**
     * Creates calendar entries for the specified task and calendar.
     * The method retrieves the calendar and task by their respective IDs,
     * and populates the calendar with the provided calendar entries.
     *
     * @param taskId the ID of the task for which calendar entries are being created
     * @param calendarId the ID of the calendar where entries will be added
     * @param calendarEntries a list of calendar entry objects containing the details of each entry to be created
     * @throws NotFoundException if the calendar with the given ID is not found
     */
    @Override
    @Transactional
    public void createCalendarEntriesForTask(
            Long taskId,
            Long calendarId,
            List<CalculatedCapacityCalendarEntryVO> calendarEntries
    ) {
        log.info("Creating calendar entries for task {} into calendar {}", taskId, calendarId);

        Calendar calendar = calendarRepository.findById(calendarId).orElseThrow(() -> new NotFoundException("Calendar not found for id " + calendarId));
        Task task = taskService.getTaskById(taskId);

        List<CalendarEntry> savedEntries = new ArrayList<>();

        List<CalculatedCapacityCalendarEntryVO> mergedEntriesWithSameDate = mergeEntriesWithSameDate(calendarEntries);

        for (CalculatedCapacityCalendarEntryVO dto : mergedEntriesWithSameDate) {
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
     * @param year       to initialize the calendar for.
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

        //fetch events from outlook calendar
        OutlookCalendarViewResponse outlookCalendarViewResponse =
                outlookCalendarService.fetchCalendarEvents(employeeMail, start, end);

        BigDecimal workingHours = calendar.getOwner().getWorkingHoursPerDay();
        List<CalendarEntry> calendarEntries = parseOutlookCalendarToEntries(outlookCalendarViewResponse, workingHours);

        try {
            for (CalendarEntry entry : calendarEntries) {
                entry.setCalendar(calendar);
                calendarEntryRepository.save(entry);
                calendar.getEntries().add(entry);
            }

            calendarRepository.save(calendar);
        } catch (Exception e) {
            log.error("Error while saving calendar entries for employee {} and year {}", employeeId, year, e);
        }

    }

    /**
     * Removes all calendar entries associated with a specified task.
     *
     * @param taskId the unique identifier of the task whose calendar entries are to be removed
     */
    @Override
    @Transactional
    public void removeCalendarEntriesOfTask(Long taskId) {
        log.info("Removing calendar entries of task {}", taskId);

        Task task = taskService.getTaskById(taskId);
        List<CalendarEntry> calendarEntries = task.getCalendarEntry();

        calendarEntryRepository.deleteAll(calendarEntries);
    }

    /**
     * Merges a list of calendar entries by grouping them based on their date and summing the duration of entries with the same date.
     *
     * @param calendarEntries the list of calendar entries to be merged, where each entry contains*/
    List<CalculatedCapacityCalendarEntryVO> mergeEntriesWithSameDate(List<CalculatedCapacityCalendarEntryVO> calendarEntries) {
        // First group everything by date in a map
        Map<LocalDate, List<CalculatedCapacityCalendarEntryVO>> groupedByDate = calendarEntries.stream()
                .collect(Collectors.groupingBy(CalculatedCapacityCalendarEntryVO::date));

        // sum durationInMinutes of all entries with same date
        List<CalculatedCapacityCalendarEntryVO> mergedEntries = groupedByDate.entrySet().stream()
                .map(entry -> {
                    LocalDate date = entry.getKey();
                    List<CalculatedCapacityCalendarEntryVO> entries = entry.getValue();

                    String title = entries.getFirst().title(); //same title
                    Long totalDuration = entries.stream()
                            .mapToLong(CalculatedCapacityCalendarEntryVO::durationInMinutes)
                            .sum();

                    return new CalculatedCapacityCalendarEntryVO(title, date, totalDuration);
                })
                .toList();

        return mergedEntries;
    }

    /**
     * Parses the outlook calendar events into calendar entries.
     *
     * @param outlookCalendarViewResponse to parse from
     * @param employeeWorkingHours        to calculate the duration of the calendar entries.
     * @return a list of calendar entries with the parsed events.
     */
    private List<CalendarEntry> parseOutlookCalendarToEntries(OutlookCalendarViewResponse outlookCalendarViewResponse, BigDecimal employeeWorkingHours) {
        List<CalendarEntry> calendarEntries = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        //format of DateTimeTimeZone of event
        //"dateTime": "2025-09-25T11:00:00",
        //"timeZone": "Europe/Berlin"
        for (OutlookCalendarEvent event : outlookCalendarViewResponse.value()) {
            //when its already imported, skip it.
            if (calendarEntryRepository.existsByOutlookLinkId(event.id())) {
                continue;
            }

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
                //entry.setCalendar(calendar);
                entry.setOutlookLinkId(event.id());
                entry.setTitle(event.subject());
                entry.setDescription(event.body().content());
                entry.setDate(curDay);
                entry.setDurationInMinutes(TimeCalculatorHelper.timeUnitToMinutes(entryHours, TimeUnit.HOUR));

                calendarEntries.add(entry);
            }
        }
        return calendarEntries;
    }
}

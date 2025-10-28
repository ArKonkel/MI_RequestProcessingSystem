package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.calendar;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.capacity.CalculatedCapacityCalendarEntryVO;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.employee.Employee;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.integration.outlook.OutlookCalendarService;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.integration.outlook.graphTypes.DateTimeTimeZone;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.integration.outlook.graphTypes.ItemBody;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.integration.outlook.graphTypes.OutlookCalendarEvent;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.integration.outlook.graphTypes.OutlookCalendarViewResponse;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.task.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class CalendarServiceImplTest {

    @Mock
    private CalendarRepository calendarRepository;

    @Mock
    private CalendarEntryRepository calendarEntryRepository;

    @Mock
    private CalendarMapper calendarMapper;

    @Mock
    private TaskService taskService;

    @Mock
    private OutlookCalendarService outlookCalendarService;

    @InjectMocks
    private CalendarServiceImpl calendarService;

    private Calendar calendar;
    private Employee employee;

    @BeforeEach
    void setUp() {
        openMocks(this);

        employee = new Employee();
        employee.setId(1L);
        employee.setEmail("freddy.mustermann@mail.de");
        employee.setWorkingHoursPerDay(BigDecimal.valueOf(8));

        calendar = new Calendar();
        calendar.setId(1L);
        calendar.setOwner(employee);
        calendar.setEntries(new HashSet<>());
    }

    @Test
    void testInitCalendarOfEmployeeOfYear_savesCalendarEntriesFromOutlookEvents() {
        //GIVEN
        Year year = Year.of(2025);

        // Outlook-Events: one normal, one with range of two days
        ZoneId berlinZone = ZoneId.of("Europe/Berlin");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        //One hour
        String day1StartStr = LocalDateTime.of(2025, 9, 24, 13, 0)
                .atZone(berlinZone)
                .format(formatter);
        String day1EndStr = LocalDateTime.of(2025, 9, 24, 14, 0)
                .atZone(berlinZone)
                .format(formatter);

        // four hours = 2x2
        String day2StartStr = LocalDateTime.of(2025, 9, 24, 22, 0)
                .atZone(berlinZone)
                .format(formatter);

        String day2EndStr = LocalDateTime.of(2025, 9, 25, 3, 0)
                .atZone(berlinZone)
                .format(formatter);


        OutlookCalendarEvent singleDayEvent = new OutlookCalendarEvent(
                "AAMkAGIAAAoZDOFAAA=",
                "Code Review",
                new DateTimeTimeZone(day1StartStr, "Europe/Berlin"),
                new DateTimeTimeZone(day1EndStr, "Europe/Berlin"),
                new ItemBody("Text", "Review der neuen API-Endpunkte.")
        );

        OutlookCalendarEvent multiDayEvent = new OutlookCalendarEvent(
                "AAMkAGIAAAoZDOFBBB=",
                "Overnight Maintenance",
                new DateTimeTimeZone(day2StartStr, "Europe/Berlin"),
                new DateTimeTimeZone(day2EndStr, "Europe/Berlin"),
                new ItemBody("Text", "Serverwartung über Nacht.")
        );

        //WHEN
        when(calendarRepository.findByOwnerId(employee.getId())).thenReturn(calendar);

        OutlookCalendarViewResponse response = new OutlookCalendarViewResponse(List.of(singleDayEvent, multiDayEvent));
        when(outlookCalendarService.fetchCalendarEvents(any(), any(), any())).thenReturn(response);

        calendarService.initCalendarOfEmployeeOfYear(employee.getId(), year);

        //check if save all went through
        int expectedNumberOfEntries = 3;
        ArgumentCaptor<CalendarEntry> captor = ArgumentCaptor.forClass(CalendarEntry.class);
        verify(calendarEntryRepository, times(expectedNumberOfEntries)).save(captor.capture());

        List<CalendarEntry> savedEntries = captor.getAllValues();

        //THEN
        // Expected: 3 CalendarEntries: 1 for "Code Review", 2 for "Overnight Maintenance" (2 Days)
        assertEquals(3, savedEntries.size());

        //outlookLinkIds and id from event are the same
        assertThat(savedEntries).extracting(CalendarEntry::getOutlookLinkId)
                .containsExactlyInAnyOrder("AAMkAGIAAAoZDOFAAA=", "AAMkAGIAAAoZDOFBBB=", "AAMkAGIAAAoZDOFBBB=");

        // check right title
        assertThat(savedEntries).extracting(CalendarEntry::getTitle)
                .containsExactlyInAnyOrder("Code Review", "Overnight Maintenance", "Overnight Maintenance");

        // Check times
        assertThat(savedEntries).extracting(CalendarEntry::getDurationInMinutes)
                .containsExactlyInAnyOrder(60L, 120L, 180L);

        // check description
        assertThat(savedEntries).extracting(CalendarEntry::getDescription)
                .containsExactlyInAnyOrder("Review der neuen API-Endpunkte.", "Serverwartung über Nacht.", "Serverwartung über Nacht.");

        // dates are the right ones
        assertThat(savedEntries).extracting(CalendarEntry::getDate)
                .containsExactlyInAnyOrder(
                        LocalDate.of(2025, 9, 24),
                        LocalDate.of(2025, 9, 24),
                        LocalDate.of(2025, 9, 25)
                );

        // every entry is in same calendar
        assertThat(savedEntries).allMatch(entry -> entry.getCalendar() == calendar);
    }

    @Test
    void testInitCalendarOfEmployeeOfYear_shouldSkipAlreadyInsertedEntries() {
        //GIVEN
        Year year = Year.of(2025);

        String alreadyInsertedEntryId = "AAMkAGIAAAoZDOFBBB=";
        CalendarEntry entry1 = new CalendarEntry();
        entry1.setId(1L);
        entry1.setDate(LocalDate.of(2025, 9, 24));
        entry1.setOutlookLinkId(alreadyInsertedEntryId);

        CalendarEntry entry2 = new CalendarEntry();
        entry2.setId(2L);
        entry2.setDate(LocalDate.of(2025, 9, 25));
        entry2.setOutlookLinkId(alreadyInsertedEntryId);

        calendar.getEntries().addAll(Arrays.asList(entry1, entry2));

        // Outlook-Events: one normal, one with range of two days
        ZoneId berlinZone = ZoneId.of("Europe/Berlin");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        //One hour
        String day1StartStr = LocalDateTime.of(2025, 9, 24, 13, 0)
                .atZone(berlinZone)
                .format(formatter);
        String day1EndStr = LocalDateTime.of(2025, 9, 24, 14, 0)
                .atZone(berlinZone)
                .format(formatter);

        // four hours = 2x2
        String day2StartStr = LocalDateTime.of(2025, 9, 24, 22, 0)
                .atZone(berlinZone)
                .format(formatter);

        String day2EndStr = LocalDateTime.of(2025, 9, 25, 3, 0)
                .atZone(berlinZone)
                .format(formatter);


        OutlookCalendarEvent singleDayEvent = new OutlookCalendarEvent(
                "AAMkAGIAAAoZDOFAAA=",
                "Code Review",
                new DateTimeTimeZone(day1StartStr, "Europe/Berlin"),
                new DateTimeTimeZone(day1EndStr, "Europe/Berlin"),
                new ItemBody("Text", "Review der neuen API-Endpunkte.")
        );

        OutlookCalendarEvent multiDayEvent = new OutlookCalendarEvent(
                "AAMkAGIAAAoZDOFBBB=",
                "Overnight Maintenance",
                new DateTimeTimeZone(day2StartStr, "Europe/Berlin"),
                new DateTimeTimeZone(day2EndStr, "Europe/Berlin"),
                new ItemBody("Text", "Serverwartung über Nacht.")
        );

        //WHEN
        when(calendarRepository.findByOwnerId(employee.getId())).thenReturn(calendar);
        when(calendarEntryRepository.existsByOutlookLinkId(alreadyInsertedEntryId)).thenReturn(true);

        OutlookCalendarViewResponse response = new OutlookCalendarViewResponse(List.of(singleDayEvent, multiDayEvent));
        when(outlookCalendarService.fetchCalendarEvents(any(), any(), any())).thenReturn(response);

        calendarService.initCalendarOfEmployeeOfYear(employee.getId(), year);

        //check if save all went through
        int expectedNumberOfEntries = 1;
        ArgumentCaptor<CalendarEntry> captor = ArgumentCaptor.forClass(CalendarEntry.class);
        verify(calendarEntryRepository, times(expectedNumberOfEntries)).save(captor.capture());

        List<CalendarEntry> savedEntries = captor.getAllValues();

        //THEN
        // Expected: 3 CalendarEntries: 1 for "Code Review", 2 for "Overnight Maintenance" (2 Days)
        assertEquals(expectedNumberOfEntries, savedEntries.size());

        //outlookLinkIds and id from event are the same
        assertThat(savedEntries).extracting(CalendarEntry::getOutlookLinkId)
                .containsExactlyInAnyOrder("AAMkAGIAAAoZDOFAAA=");

        // check right title
        assertThat(savedEntries).extracting(CalendarEntry::getTitle)
                .containsExactlyInAnyOrder("Code Review");

        // Check times
        assertThat(savedEntries).extracting(CalendarEntry::getDurationInMinutes)
                .containsExactlyInAnyOrder(60L);

        // check description
        assertThat(savedEntries).extracting(CalendarEntry::getDescription)
                .containsExactlyInAnyOrder("Review der neuen API-Endpunkte.");

        // dates are the right ones
        assertThat(savedEntries).extracting(CalendarEntry::getDate)
                .containsExactlyInAnyOrder(
                        LocalDate.of(2025, 9, 24)
                );

        // every entry is in same calendar
        assertThat(savedEntries).allMatch(entry -> entry.getCalendar() == calendar);
    }

    @Test
    void mergeEntriesWithSameDate_shouldSumDurationsByDate() {
        // GIVEN
        List<CalculatedCapacityCalendarEntryVO> entries = List.of(
                new CalculatedCapacityCalendarEntryVO("Task X", LocalDate.of(2025, 10, 28), 60L),
                new CalculatedCapacityCalendarEntryVO("Task X", LocalDate.of(2025, 10, 28), 30L),
                new CalculatedCapacityCalendarEntryVO("Task X", LocalDate.of(2025, 10, 29), 120L),
                new CalculatedCapacityCalendarEntryVO("Task Y", LocalDate.of(2025, 10, 28), 45L)
        );

        // WHEN
        List<CalculatedCapacityCalendarEntryVO> merged = calendarService.mergeEntriesWithSameDate(entries);

        // THEN
        //Should have two entries for 28.10. and 29.10.
        assertEquals(2, merged.size());

        // Check sum: 28.10. = 60+30+45 = 135, 29.10. = 120
        Map<LocalDate, Long> expectedDurations = Map.of(
                LocalDate.of(2025, 10, 28), 135L,
                LocalDate.of(2025, 10, 29), 120L
        );

        merged.forEach(e -> assertEquals(expectedDurations.get(e.date()), e.durationInMinutes()));
    }
}

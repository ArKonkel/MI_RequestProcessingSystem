package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.calendar;

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
import java.util.List;
import java.util.Set;

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
        calendar.setEntries(Set.of());
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
                1L,
                "Code Review",
                new DateTimeTimeZone(day1StartStr, "Europe/Berlin"),
                new DateTimeTimeZone(day1EndStr, "Europe/Berlin"),
                new ItemBody("Text", "Review der neuen API-Endpunkte.")
        );

        OutlookCalendarEvent multiDayEvent = new OutlookCalendarEvent(
                2L,
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
        ArgumentCaptor<List<CalendarEntry>> captor = ArgumentCaptor.forClass(List.class);
        verify(calendarEntryRepository, times(1)).saveAll(captor.capture());
        List<CalendarEntry> savedEntries = captor.getValue();

        //THEN
        // Expected: 3 CalendarEntries: 1 for "Code Review", 2 for "Overnight Maintenance" (2 Days)
        assertEquals(3, savedEntries.size());

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
}

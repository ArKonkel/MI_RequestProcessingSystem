package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.calendar;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/calendars")
@PreAuthorize("hasAnyRole('ADMIN', 'CAPACITY_PLANNER')")
public class CalendarController {

    private final CalendarService calendarService;

    @GetMapping("/{employeeId}/{from}/{to}")
    public ResponseEntity<CalendarDto> getCalendarOfEmployee(@PathVariable Long employeeId, @PathVariable LocalDate from, @PathVariable LocalDate to) {
        log.info("REST request to get calendar of employee {} from {} to {}", employeeId, from, to);

        CalendarDto calendar = calendarService.getCalendarDtoOfEmployee(employeeId, from, to);

        return ResponseEntity.ok().body(calendar);
    }

    @GetMapping("/{from}/{to}")
    public ResponseEntity<List<CalendarDto>> getAllCalendar(@PathVariable LocalDate from, @PathVariable LocalDate to) {
        log.info("REST request to get all calendars from {} to {}", from, to);

        List<CalendarDto> calendars = calendarService.getAllCalendars(from, to);

        return ResponseEntity.ok().body(calendars);
    }

    @PostMapping("/{employeeId}/{year}")
    public ResponseEntity<Void> initCalendarOfEmployeeOfYear(@PathVariable Long employeeId, @PathVariable Integer year) {
        log.info("REST request to init calendar of employee {} for year {}", employeeId, year);

        calendarService.initCalendarOfEmployeeOfYear(employeeId, Year.of(year));

        return ResponseEntity.ok().build();
    }
}

package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.calendar;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.Year;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/calendars")
public class CalendarController {

    private final CalendarService calendarService;

    @GetMapping("/{employeeId}/{from}/{to}")
    public ResponseEntity<CalendarDto> getCalendarOfEmployee(@PathVariable Long employeeId, @PathVariable LocalDate from, @PathVariable LocalDate to) {
        log.info("REST request to get calendar of employee {} from {} to {}", employeeId, from, to);

        CalendarDto calendar = calendarService.getCalendarDtoOfEmployee(employeeId, from, to);

        return ResponseEntity.ok().body(calendar);
    }

    @PostMapping("/{employeeId}/{year}")
    public ResponseEntity<Void> initCalendarOfEmployeeOfYear(@PathVariable Long employeeId, @PathVariable Integer year) {
        log.info("REST request to init calendar of employee {} for year {}", employeeId, year);

        calendarService.initCalendarOfEmployeeOfYear(employeeId, Year.of(year));

        return ResponseEntity.ok().build();
    }
}

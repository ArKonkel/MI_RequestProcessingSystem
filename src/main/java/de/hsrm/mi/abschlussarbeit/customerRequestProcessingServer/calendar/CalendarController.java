package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.calendar;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/calendar")
public class CalendarController {

    private final CalendarService calendarService;

    @GetMapping("/{employeeId}/{from}/{to}")
    public ResponseEntity<CalendarDto> getCalendarOfEmployee(@PathVariable Long employeeId, @PathVariable LocalDate from, @PathVariable LocalDate to) {
        log.info("REST request to get calendar of employee {} from {} to {}", employeeId, from, to);

        CalendarDto calendar = calendarService.getCalendarOfEmployee(employeeId, from, to);

        return ResponseEntity.ok().body(calendar);
    }
}

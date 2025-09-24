package de.hsrm.mi.outlookdummy.microsoftGraphAPI;

import de.hsrm.mi.outlookdummy.microsoftGraphAPI.types.OutlookCalendarViewResponse;
import de.hsrm.mi.outlookdummy.microsoftGraphAPI.types.SendMailRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;

@Slf4j
@RestController
@RequestMapping("/v1.0")
@AllArgsConstructor
public class GraphController {

    private final MailService mailService;

    private final OutlookCalendarService calendarService;

    @GetMapping("/users/{userPrincipalName}/calendar/calendarView")
    public ResponseEntity<OutlookCalendarViewResponse> getCalendarViewOfPrincipal(@PathVariable String userPrincipalName,
                                                                                  @RequestParam OffsetDateTime start, @RequestParam OffsetDateTime end) {
        log.info("REST request to get calendar view of {}", userPrincipalName);

        OutlookCalendarViewResponse response = calendarService.getCalendarView(userPrincipalName, start.toString(), end.toString());

        if (response == null || response.value().isEmpty())
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/users/{userPrincipalName}/sendMail")
    public ResponseEntity<Void> sendMail(
            @PathVariable String userPrincipalName,
            @Valid @RequestBody SendMailRequest request) {

        log.info("REST request to send mail from {} to {}", userPrincipalName, request.message().toRecipients());

        try {
            mailService.sendMail(userPrincipalName, request);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error sending mail for user {}: {}", userPrincipalName, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

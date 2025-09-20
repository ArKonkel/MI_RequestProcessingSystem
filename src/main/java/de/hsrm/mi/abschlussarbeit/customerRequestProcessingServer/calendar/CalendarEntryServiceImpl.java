package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.calendar;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class CalendarEntryServiceImpl implements CalendarEntryService {

    private final CalendarEntryRepository calendarEntryRepository;


}

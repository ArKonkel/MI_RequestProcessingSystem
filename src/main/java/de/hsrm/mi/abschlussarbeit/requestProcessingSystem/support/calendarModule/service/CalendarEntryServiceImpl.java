package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.calendarModule.service;


import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.calendarModule.repository.CalendarEntryRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class CalendarEntryServiceImpl implements CalendarEntryService {

    private final CalendarEntryRepository calendarEntryRepository;


}

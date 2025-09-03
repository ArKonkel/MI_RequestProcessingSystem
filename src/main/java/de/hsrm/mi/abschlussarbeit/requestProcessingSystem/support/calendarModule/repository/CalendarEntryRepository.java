package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.calendarModule.repository;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.calendarModule.entity.CalendarEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CalendarEntryRepository extends JpaRepository<CalendarEntry, Long> {
}

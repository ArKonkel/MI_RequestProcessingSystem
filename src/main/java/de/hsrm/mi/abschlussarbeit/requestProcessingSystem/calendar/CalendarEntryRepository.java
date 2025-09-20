package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.calendar;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CalendarEntryRepository extends JpaRepository<CalendarEntry, Long> {
}

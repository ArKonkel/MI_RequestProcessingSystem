package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.calendarModule.repository;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.calendarModule.entity.Calendar;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CalendarRepository extends JpaRepository<Calendar, Long> {
}

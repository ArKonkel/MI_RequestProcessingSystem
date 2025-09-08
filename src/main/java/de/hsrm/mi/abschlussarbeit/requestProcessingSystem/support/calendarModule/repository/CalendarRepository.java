package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.calendarModule.repository;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.calendarModule.entity.Calendar;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CalendarRepository extends JpaRepository<Calendar, Long> {

    List<Calendar> findByEmployeeId(Long employeeId);
}

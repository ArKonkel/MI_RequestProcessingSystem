package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.calendar;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CalendarRepository extends JpaRepository<Calendar, Long> {

    List<Calendar> findByEmployeeId(Long employeeId);
}

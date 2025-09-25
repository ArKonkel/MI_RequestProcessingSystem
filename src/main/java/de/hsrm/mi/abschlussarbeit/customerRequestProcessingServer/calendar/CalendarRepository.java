package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.calendar;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CalendarRepository extends JpaRepository<Calendar, Long> {

    Calendar findByOwnerId(Long employeeId);
}

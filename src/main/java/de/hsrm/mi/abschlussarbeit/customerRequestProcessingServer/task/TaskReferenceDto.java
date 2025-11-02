package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.task;

import java.time.LocalDate;
import java.util.List;

public record TaskReferenceDto(Long id, String title, TaskStatus status, List<LocalDate> calendarEntryDates, boolean isAlreadyPlanned, LocalDate dueDate) {
}

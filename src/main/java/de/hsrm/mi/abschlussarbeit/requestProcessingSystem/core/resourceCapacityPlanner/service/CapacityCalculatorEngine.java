package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.resourceCapacityPlanner.service;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.taskManager.dto.TaskDto;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.calendarModule.dto.CalendarEntryDto;

import java.time.LocalDate;
import java.util.List;

public interface CapacityCalculatorEngine {

    List<CalendarEntryDto> calculateNextFreeCapacity(TaskDto taskDto, Long employeeId, LocalDate from, LocalDate to);
}

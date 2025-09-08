package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.resourceCapacityPlanner.service;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.resourceCapacityPlanner.exception.NoCapacityUntilDueDateException;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.taskManager.dto.TaskDto;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.calendarModule.dto.CalendarDto;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.calendarModule.dto.CalendarEntryDto;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.calendarModule.service.CalendarModule;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.userManager.dto.EmployeeDto;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.userManager.service.UserManager;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class CapacityCalculatorEngineImpl implements CapacityCalculatorEngine {

    private CalendarModule calendarModule;

    private UserManager userManager;


    /**
     * Methode to calculate the next free slot for a given task and employee.
     *
     * @param taskDto    the task to calculate the free capacity for
     * @param employeeId the employee to calculate the free capacity for
     * @param from       the start date of the calculation
     * @param to         the end date of the calculation
     * @return a list of free calendar entries for the given task and employee
     */
    @Override
    public List<CalendarEntryDto> calculateNextFreeCapacity(TaskDto taskDto, Long employeeId, LocalDate from, LocalDate to) {
        EmployeeDto employeeDto = userManager.getEmployeeById(employeeId);
        long dailyWorkingMinutes = employeeDto.workingHoursPerDay().longValue() * 60;

        CalendarDto calendarDto = calendarModule.getCalendarOfEmployee(employeeId, from, to);
        List<CalendarEntryDto> calendarEntryDtos = calendarDto.entries();

        Long remainingTaskTime = taskDto.estimatedTime();
        List<CalendarEntryDto> createdSlots = new ArrayList<>();

        // Iterate over each day in the range
        for (LocalDate date = from; !date.isAfter(to); date = date.plusDays(1)) {
            Long occupiedMinutes = 0L;

            // Only proceed if there is still task time left to schedule
            if (remainingTaskTime > 0) {
                // Calculate occupied time for this day
                for (CalendarEntryDto entry : calendarEntryDtos) {
                    if (entry.date().equals(date)) {
                        occupiedMinutes += entry.duration();
                    }
                }

                long freeMinutes = dailyWorkingMinutes - occupiedMinutes;

                if (freeMinutes > remainingTaskTime) {
                    freeMinutes = remainingTaskTime;
                }

                // Create new slot if there is free time
                if (freeMinutes > 0) {
                    CalendarEntryDto newSlot = new CalendarEntryDto(
                            null,
                            taskDto.processItem().title(),
                            taskDto.processItem().description(),
                            date,
                            freeMinutes
                    );
                    createdSlots.add(newSlot);
                    remainingTaskTime -= freeMinutes;
                }
            } else {
                //don't check next days, when no task time left to schedule
                break;
            }
        }

        if (remainingTaskTime > 0) {
            throw new NoCapacityUntilDueDateException("No capacity for task " + taskDto.processItem().id() + " until due date");
        }

        return createdSlots;
    }
}
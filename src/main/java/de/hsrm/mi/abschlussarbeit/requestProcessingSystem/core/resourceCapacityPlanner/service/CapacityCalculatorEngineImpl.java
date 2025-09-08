package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.resourceCapacityPlanner.service;

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
     * @param taskDto the task to calculate the free capacity for
     * @param employeeId the employee to calculate the free capacity for
     * @param from the start date of the calculation
     * @param to the end date of the calculation
     * @return a list of free calendar entries for the given task and employee
     */
    @Override
    public List<CalendarEntryDto> calculateNextFreeCapacity(TaskDto taskDto, Long employeeId, LocalDate from, LocalDate to) {
        EmployeeDto employeeDto = userManager.getEmployeeById(employeeId);
        long dailyWorkingMinutes = employeeDto.workingHoursPerDay().longValue() * 60;

        CalendarDto calendarDto = calendarModule.getCalendarOfEmployee(employeeId, from, to);
        List<CalendarEntryDto> calendarEntryDtos = calendarDto.entries();

        for (LocalDate date = from; !date.isAfter(to); date = date.plusDays(1)) {
            Long occupiedTime = 0L;

            for (CalendarEntryDto calendarEntryDto : calendarEntryDtos) {
                if (calendarEntryDto.date().equals(date)) {
                    occupiedTime += calendarEntryDto.duration();
                }
            }

            //when the task fits in the working day
            if (occupiedTime + taskDto.estimatedTime() <= dailyWorkingMinutes) {
                return List.of(new CalendarEntryDto (
                        null, taskDto.processItem().title(), taskDto.processItem().description(), date, taskDto.estimatedTime()
                ));
            }

        }

        //no free capacity found
        return List.of();
    }
}

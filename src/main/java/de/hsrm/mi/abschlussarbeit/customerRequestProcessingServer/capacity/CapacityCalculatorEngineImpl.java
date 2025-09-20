package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.capacity;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.task.TaskDto;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.calendar.CalendarDto;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.calendar.CalendarEntryDto;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.calendar.CalendarModule;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.employee.EmployeeDto;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.user.UserManager;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

@Service
@AllArgsConstructor
@Slf4j
public class CapacityCalculatorEngineImpl implements CapacityCalculatorEngine {

    private CalendarModule calendarModule;

    private UserManager userManager;


    /**
     * Methode to calculate the next free slot for a given task and employee. Skips Weekends.
     *
     * @param taskDto    the task to calculate the free capacity for
     * @param employeeId the employee to calculate the free capacity for
     * @param from       the start date of the calculation
     * @param to         the end date of the calculation
     * @return a list of free calendar entries for the given task and employee
     */
    @Override
    public List<CalculatedCapacityCalendarEntryDto> calculateFreeCapacity(TaskDto taskDto, Long employeeId, LocalDate from, LocalDate to) {
        log.info("Calculating free capacity for task {} for employee {}", taskDto.processItem().id(), employeeId);

        EmployeeDto employeeDto = userManager.getEmployeeById(employeeId);
        long dailyWorkingMinutes = employeeDto.workingHoursPerDay().longValue() * 60;

        CalendarDto calendarDto = calendarModule.getCalendarOfEmployee(employeeId, from, to);
        List<CalendarEntryDto> calendarEntryDtos = calendarDto.entries();

        Long remainingTaskTime = taskDto.estimatedTime();
        List<CalculatedCapacityCalendarEntryDto> calculatedSlots = new ArrayList<>();

        // Iterate over each day in the range
        for (LocalDate date = from; !date.isAfter(to); date = date.plusDays(1)) {
            Long occupiedMinutes = 0L;

            // Skip weekend days
            if (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
                continue;
            }

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
                    CalculatedCapacityCalendarEntryDto newSlot = new CalculatedCapacityCalendarEntryDto(
                            taskDto.processItem().title(),
                            date,
                            freeMinutes
                    );
                    calculatedSlots.add(newSlot);
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

        return calculatedSlots;
    }

    @Override
    public List<EmployeeDto> calculateEmployeesAbleToCompleteTaskEarliest(Map<EmployeeDto, List<CalculatedCapacityCalendarEntryDto>> employeeWithCalendarEntriesOfTask) {
        log.info("Calculating employees able to complete task earliest");

        Map<EmployeeDto, CalculatedCapacityCalendarEntryDto> latestEntriesOfEmployees = new HashMap<>();
        Map<EmployeeDto, CalculatedCapacityCalendarEntryDto> earliestEntries = new HashMap<>();
        List<EmployeeDto> earliestEmployees = new ArrayList<>();

        //Find first the latest entries for each List of entries of the task
        for (EmployeeDto employee : employeeWithCalendarEntriesOfTask.keySet()) {
            List<CalculatedCapacityCalendarEntryDto> entries = employeeWithCalendarEntriesOfTask.get(employee);

            CalculatedCapacityCalendarEntryDto latestEntry =
                    entries.stream().max(Comparator.comparing(CalculatedCapacityCalendarEntryDto::date)).orElseThrow();

            latestEntriesOfEmployees.put(employee, latestEntry);
        }

        //Second: Find min date of latestEntriesOfEmployees
        LocalDate earliestDate = latestEntriesOfEmployees.values().stream()
                .map(entry -> entry.date())
                .min((date1, date2) -> date1.compareTo(date2))
                .orElseThrow();

        for (EmployeeDto employee : latestEntriesOfEmployees.keySet()) {
            if (latestEntriesOfEmployees.get(employee).date().equals(earliestDate)) {

                earliestEntries.put(employee, latestEntriesOfEmployees.get(employee));
            }
        }

        // Third: Find the shortest entry (smallest duration)
        long minDuration = earliestEntries.values().stream()
                .map(entry -> entry.duration())
                .min((duration1, duration2) -> duration1.compareTo(duration2))
                .orElseThrow();

        for (EmployeeDto employee : earliestEntries.keySet()) {
            if (earliestEntries.get(employee).duration() == minDuration) {
                earliestEmployees.add(employee);
            }
        }

        return earliestEmployees;
    }
}
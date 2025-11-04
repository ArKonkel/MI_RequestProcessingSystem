package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.shared;

import java.time.DayOfWeek;
import java.time.LocalDate;

public abstract class Utils {

    public static LocalDate addWorkDays(LocalDate start, long workDays) {
        LocalDate result = start;
        long addedDays = 0;
        while (addedDays < workDays) {
            result = result.plusDays(1);
            if (!(result.getDayOfWeek() == DayOfWeek.SATURDAY || result.getDayOfWeek() == DayOfWeek.SUNDAY)) {
                addedDays++;
            }
        }
        return result;
    }
}

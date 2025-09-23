package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.shared;

import java.math.BigDecimal;
import java.math.RoundingMode;

public abstract class ToMinutesCalculator {

    public static Long timeUnitToMinutes(BigDecimal value, TimeUnit unit) {
        if (value == null || unit == null) {
            return null;
        }

        BigDecimal minutesInHour = BigDecimal.valueOf(60);
        BigDecimal hoursInDay = BigDecimal.valueOf(8);
        BigDecimal daysInWeek = BigDecimal.valueOf(5);
        BigDecimal weeksInMonth = BigDecimal.valueOf(4);

        BigDecimal totalMinutes;

        switch (unit) {
            case MINUTES:
                totalMinutes = value;
                break;
            case HOUR:
                totalMinutes = value.multiply(minutesInHour);
                break;
            case DAY:
                totalMinutes = value.multiply(hoursInDay).multiply(minutesInHour);
                break;
            case WEEK:
                totalMinutes = value.multiply(daysInWeek)
                        .multiply(hoursInDay)
                        .multiply(minutesInHour);
                break;
            case MONTH:
                totalMinutes = value.multiply(weeksInMonth)
                        .multiply(daysInWeek)
                        .multiply(hoursInDay)
                        .multiply(minutesInHour);
                break;
            default:
                return null;
        }

        return totalMinutes.setScale(0, RoundingMode.HALF_UP).longValue();
    }
}

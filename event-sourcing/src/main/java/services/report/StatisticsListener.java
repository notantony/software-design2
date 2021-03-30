package services.report;

import java.time.DayOfWeek;
import java.time.Period;
import java.util.*;

public class WeeklyStatistics {
    private final Map<DayOfWeek, List<Period>> dayDurations;

    public WeeklyStatistics() {
        dayDurations = new HashMap<>();
        for (DayOfWeek dow : DayOfWeek.values()) {
            dayDurations.put(dow, new ArrayList<>());
        }
    }

    public void update(Date date) {
        DayOfWeek dow = DayOfWeek.from(date.toInstant());
        dayDurations.get(dow)
    }
}

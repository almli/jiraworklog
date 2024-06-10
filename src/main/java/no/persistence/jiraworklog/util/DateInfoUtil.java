package no.persistence.jiraworklog.util;

import java.time.LocalDate;
import java.util.Map;

public class DateInfoUtil {
    private static final Map<Integer, String> DAG_NAVN = Map.of(
            1, "mandag",
            2, "tirsdag",
            3, "onsdag",
            4, "torsdag",
            5, "fredag",
            6, "lørdag",
            7, "søndag");

    public static String getDateInfo(LocalDate date) {
        Holiday holiday = new NorwegianHolidays().getHolidayMap(date.getYear()).get(date);
        if (holiday != null) {
            return holiday.name;
        }
        return DAG_NAVN.get(date.getDayOfWeek().getValue());
    }
}

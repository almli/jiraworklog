package no.persistence.jiraworklog.util;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public class DateUtil {
    private static final DateTimeFormatter yearMonthFormatter = DateTimeFormatter.ofPattern("yyyyMM");

    public static YearMonth parseYearMonth(String str) {
        return YearMonth.parse(str, yearMonthFormatter);
    }

    public static String formatYearMonth(YearMonth ym) {
        return ym.format(yearMonthFormatter);
    }
}

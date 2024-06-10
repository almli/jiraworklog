package no.persistence.jiraworklog.util;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NorwegianHolidays {

    private static LocalDate getEasterSundayDate(int year) {
        int a = year % 19, b = year / 100, c = year % 100, d = b / 4, e = b % 4, g = (8 * b + 13) / 25, h = (19 * a + b - d - g + 15) % 30, j = c / 4, k = c % 4, m = (a + 11 * h) / 319, r = (2 * e + 2 * j - k - h + m + 32) % 7, month = (h - m + r + 90) / 25, day = (h - m + r + month + 19) % 32;
        return LocalDate.of(year, month, day);
    }

    public List<Holiday> getHolidays(int year) {
        LocalDate easterSunday = getEasterSundayDate(year);
        List<Holiday> holidays = new ArrayList<>();
        holidays.add(new Holiday("Nyttårsdag", LocalDate.of(year, 1, 1)));
        holidays.add(new Holiday("Skjærtorsdag", easterSunday.minusDays(3)));
        holidays.add(new Holiday("Langfredag", easterSunday.minusDays(2)));
        holidays.add(new Holiday("1. påskedag", easterSunday));
        holidays.add(new Holiday("2. påskedag", easterSunday.plusDays(1)));
        holidays.add(new Holiday("1. mai", LocalDate.of(year, 5, 1)));
        holidays.add(new Holiday("17. mai", LocalDate.of(year, 5, 17)));
        holidays.add(new Holiday("Kristi hf", easterSunday.plusDays(39)));
        holidays.add(new Holiday("1. pinsedag", easterSunday.plusDays(49)));
        holidays.add(new Holiday("2. pinsedag", easterSunday.plusDays(50)));
        holidays.add(new Holiday("1. juledag", LocalDate.of(year, 12, 25)));
        holidays.add(new Holiday("2. juledag", LocalDate.of(year, 12, 26)));
        return holidays;
    }

    public Map<LocalDate, Holiday> getHolidayMap(int year) {
        return getHolidays(year).stream().collect(Collectors.toMap(h -> h.date, h -> h));
    }
}

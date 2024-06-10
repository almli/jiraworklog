package no.persistence.jiraworklog.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NorwegianHolidaysTest {

    @Test
    public void getHolidays() {
        Map<LocalDate, Holiday> map = new NorwegianHolidays().getHolidayMap(2023);
        assertEquals("Nyttårsdag", map.get(LocalDate.of(2023, 1, 1)).name);
        assertEquals("Skjærtorsdag", map.get(LocalDate.of(2023, 4, 6)).name);
        assertEquals("Langfredag", map.get(LocalDate.of(2023, 4, 7)).name);
        assertEquals("1. påskedag", map.get(LocalDate.of(2023, 4, 9)).name);
        assertEquals("2. påskedag", map.get(LocalDate.of(2023, 4, 10)).name);
        assertEquals("1. mai", map.get(LocalDate.of(2023, 5, 1)).name);
        assertEquals("17. mai", map.get(LocalDate.of(2023, 5, 17)).name);
        assertEquals("Kristi hf", map.get(LocalDate.of(2023, 5, 18)).name);
        assertEquals("1. pinsedag", map.get(LocalDate.of(2023, 5, 28)).name);
        assertEquals("2. pinsedag", map.get(LocalDate.of(2023, 5, 29)).name);
        assertEquals("1. juledag", map.get(LocalDate.of(2023, 12, 25)).name);
        assertEquals("2. juledag", map.get(LocalDate.of(2023, 12, 26)).name);
    }
}
package no.persistence.jiraworklog.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FileNameUtilTest {

    @Test
    void isTimlelisteFileName() {
        assertEquals(true, FileNameUtil.isTimlelisteFileName("timeliste_202401.csv", "csv"));
        assertEquals(false, FileNameUtil.isTimlelisteFileName("aktiviteter_202401.csv","csv"));
    }

    @Test
    void getYearMonthFromFileName() {
        assertEquals(DateUtil.parseYearMonth("202401"), FileNameUtil.getYearMonthFromFileName("timeliste_202401.csv","csv"));
    }

    @Test
    void getYearMonthFromFileNameInvalid() {
        assertThrows(IllegalArgumentException.class, () -> FileNameUtil.getYearMonthFromFileName("2021-aktiviteter.csv","csv"));
        assertThrows(IllegalArgumentException.class, () -> FileNameUtil.getYearMonthFromFileName("202101aktiviteter.csv","csv"));
        assertThrows(IllegalArgumentException.class, () -> FileNameUtil.getYearMonthFromFileName("aktiviteter.csv","csv"));
        assertThrows(IllegalArgumentException.class, () -> FileNameUtil.getYearMonthFromFileName("aktiviteter.csv","csv"));
    }

    @Test
    void getYearMonthFromFileNameNull() {
        assertThrows(IllegalArgumentException.class, () -> FileNameUtil.getYearMonthFromFileName(null,"csv"));
    }
}
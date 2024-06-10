package no.persistence.jiraworklog;

import no.persistence.jiraworklog.model.AktivitetDef;
import no.persistence.jiraworklog.model.DatoAktivitet;
import no.persistence.jiraworklog.model.DatoAktivitetKey;
import no.persistence.jiraworklog.util.DateInfoUtil;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Strings.isNullOrEmpty;

public class TimelisteIO {
    public static List<DatoAktivitet> readFromString(String timeliste) {
        List<DatoAktivitet> list = new ArrayList<>();
        try (Reader reader = new StringReader(timeliste);) {
            CSVFormat format = CSVFormat.DEFAULT.withDelimiter(';');
            Iterable<CSVRecord> records = format.parse(reader);
            boolean isFirstRecord = true;
            for (CSVRecord record : records) {
                if (isFirstRecord) {
                    isFirstRecord = false;
                    continue; // skip header
                }

                if (isNullOrEmpty(record.get(3))) {
                    continue;
                }
                DatoAktivitet rta = new DatoAktivitet();
                rta.dato = LocalDate.parse(record.get(1));
                rta.aktivitet = record.get(2);
                rta.timer = Float.parseFloat(record.get(3));
                rta.kommentar = record.get(4);
                list.add(rta);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public static void writeToString(YearMonth yearMonth, String filename, List<String> aktiviteter, LocalDate startDate) {
        try {
            FileWriter fw = new FileWriter(filename);
            LocalDate endDate = startDate.plusMonths(1).minusDays(1);
            LocalDate date = LocalDate.of(startDate.getYear(), startDate.getMonth(), startDate.getDayOfMonth());
            do {
                fw.write("ukedag;dato;aktivitet;timer;kommentar\n");
                for (String aktivitet : aktiviteter) {
                    fw.write(adjustStringLength(DateInfoUtil.getDateInfo(date), 7) + ";" + date + ";" + aktivitet + ";;;\n");
                }
                date = date.plusDays(1);
                fw.flush();
            } while (!date.isAfter(endDate));
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    public static String writeToString(YearMonth yearMonth, List<AktivitetDef> aktivitetDefList, List<DatoAktivitet> aktiviteter) {

        StringWriter sw = new StringWriter();
        sw.write("ukedag;dato;aktivitet;timer;kommentar\n");
        LocalDate date = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        do {
            for (AktivitetDef aktivitet : aktivitetDefList) {
                sw.write(adjustStringLength(DateInfoUtil.getDateInfo(date), 7) + ";" + date + ";" + aktivitet.id + ";" + findTimer(aktiviteter, date, aktivitet.id) + ";;\n");
            }
            date = date.plusDays(1);

        } while (!date.isAfter(endDate));
        return sw.toString();

    }

    private static String findTimer(List<DatoAktivitet> aktiviteter, LocalDate date, String aktivitetId) {
        float timer = 0;
        for (DatoAktivitet aktivitet : aktiviteter) {
            if (aktivitet.dato.equals(date) && aktivitet.aktivitet.equals(aktivitetId)) {
                timer += aktivitet.timer;
            }
        }
        return timer > 0.1 ? String.valueOf(timer) : "";
    }

    static String adjustStringLength(String input, int length) {
        if (input.length() < length) {
            // If the string is shorter than the specified length, we pad it with spaces.
            return String.format("%1$-" + length + "s", input);
        } else {
            // If the string is longer than the specified length, we cut it.
            return input.substring(0, length);
        }
    }

    public static Map<DatoAktivitetKey, String> toActivityDays(YearMonth yearMonth, List<AktivitetDef> aktivitetDefList, List<DatoAktivitet> aktiviteter) {
        LocalDate date = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        Map<DatoAktivitetKey, String> map = new HashMap<>();
        do {
            for (AktivitetDef aktivitet : aktivitetDefList) {
                String timer = findTimer(aktiviteter, date, aktivitet.id);
                if (timer.length() > 0) {
                    map.put(new DatoAktivitetKey(date, aktivitet.id), adjustStringLength(DateInfoUtil.getDateInfo(date), 7) + ";" + date + ";" + aktivitet.id + ";" + timer + ";;");
                }
            }
            date = date.plusDays(1);

        } while (!date.isAfter(endDate));

        return map;
    }
}

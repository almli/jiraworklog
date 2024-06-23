package no.persistence.jiraworklog.util;

import org.jetbrains.annotations.NotNull;

import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileNameUtil {

    public static boolean isTimlelisteFileName(String fileName, String filtype) {
        if (fileName == null) {
            return false;
        }
        return patternForFiltype(filtype).matcher(fileName).matches();
    }

    private static @NotNull Pattern patternForFiltype(String filtype) {
        return Pattern.compile("^timeliste_(\\d{6})\\." + filtype + "$");
    }

    public static YearMonth getYearMonthFromFileName(String fileName, String filtype) {
        if (fileName == null) {
            throw new IllegalArgumentException("Filename is null");
        }
        Matcher matcher = patternForFiltype(filtype).matcher(fileName);
        if (matcher.matches()) {
            String yearMonthString = matcher.group(1);
            try {
                return DateUtil.parseYearMonth(yearMonthString);
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Invalid filename: " + fileName, e);
            }
        }
        throw new IllegalArgumentException("Invalid filename: " + fileName);
    }

    public static String toTimelisteFileName(String suffix, YearMonth yearMonth, String filtype) {
        return "timeliste_" + DateUtil.formatYearMonth(yearMonth) + suffix + "." + filtype;
    }
}

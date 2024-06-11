package no.persistence.jiraworklog.util;

import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileNameUtil {
    private static final Pattern TIMELISTE_PATTERN = Pattern.compile("^timeliste_(\\d{6})\\.csv$");

    public static boolean isTimlelisteFileName(String fileName) {
        if (fileName == null) {
            return false;
        }
        return TIMELISTE_PATTERN.matcher(fileName).matches();
    }

    public static YearMonth getYearMonthFromFileName(String fileName) {
        if (fileName == null) {
            throw new IllegalArgumentException("Filename is null");
        }
        Matcher matcher = TIMELISTE_PATTERN.matcher(fileName);
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

    public static String toTimelisteFileName(String suffix, YearMonth yearMonth) {
        return "timeliste_" + DateUtil.formatYearMonth(yearMonth) + suffix + ".csv";
    }
}

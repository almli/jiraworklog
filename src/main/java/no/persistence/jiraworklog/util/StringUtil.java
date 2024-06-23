package no.persistence.jiraworklog.util;

public class StringUtil {
    public static String adjustStringLength(String input, int length) {
        if (input.length() < length) {
            // If the string is shorter than the specified length, we pad it with spaces.
            return String.format("%1$-" + length + "s", input);
        } else {
            // If the string is longer than the specified length, we cut it.
            return input.substring(0, length);
        }
    }
}

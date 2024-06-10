package no.persistence.jiraworklog.util;

import java.time.LocalDate;

public class Holiday {
    public final String name;
    public final LocalDate date;

    public Holiday(String name, LocalDate date) {
        this.name = name;
        this.date = date;
    }
}

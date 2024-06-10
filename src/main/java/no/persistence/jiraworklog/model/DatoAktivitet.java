package no.persistence.jiraworklog.model;

import java.time.LocalDate;

public class DatoAktivitet {
    public String id;
    public LocalDate dato;
    public String aktivitet;
    public String kommentar;
    public float timer;
    public String authorDisplayName;
    public String authorEmail;

    @Override
    public String toString() {
        return "DatoAktivitet{" +
                "id='" + id + '\'' +
                ", dato=" + dato +
                ", aktivitet='" + aktivitet + '\'' +
                ", kommentar='" + kommentar + '\'' +
                ", timer=" + timer +
                ", authorDisplayName='" + authorDisplayName + '\'' +
                ", authorEmail='" + authorEmail + '\'' +
                '}';
    }
}

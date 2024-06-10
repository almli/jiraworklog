package no.persistence.jiraworklog.model;

import java.time.LocalDate;
import java.util.Objects;

public class DatoAktivitetKey {
    public final LocalDate dato;
    public final String aktivitetId;

    public DatoAktivitetKey(LocalDate dato, String aktivitetId) {
        this.dato = dato;
        this.aktivitetId = aktivitetId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DatoAktivitetKey that = (DatoAktivitetKey) o;
        return Objects.equals(dato, that.dato) && Objects.equals(aktivitetId, that.aktivitetId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dato, aktivitetId);
    }
}

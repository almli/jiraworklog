package no.persistence.jiraworklog;

import no.persistence.jiraworklog.model.DatoAktivitet;

import java.util.List;

public class Diff {
    public List<DatoAktivitet> missingJira;
    public List<DatoAktivitet> missingLocal;
}

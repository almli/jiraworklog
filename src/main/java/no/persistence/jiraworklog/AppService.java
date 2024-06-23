package no.persistence.jiraworklog;

import no.persistence.jiraworklog.model.AktivitetDef;
import no.persistence.jiraworklog.model.DatoAktivitet;
import no.persistence.jiraworklog.model.Konfig;
import no.persistence.jiraworklog.model.PushDesc;
import no.persistence.jiraworklog.util.DateUtil;

import java.io.IOException;
import java.time.*;
import java.util.ArrayList;
import java.util.List;

import static no.persistence.jiraworklog.util.DatoAktivitetUtil.getPushDesc;

public class AppService {

    private final DbService dbService;
    private final JiraIssueService jiraIssueService;

    public AppService(DbService dbService, JiraIssueService jiraIssueService) {
        this.dbService = dbService;
        this.jiraIssueService = jiraIssueService;
    }

    public void pull(YearMonth yearMonth) throws IOException {
        List<DatoAktivitet> aktivitetList = getFromJira(yearMonth);
        dbService.save(yearMonth, aktivitetList, "_pull");
    }

    private List<DatoAktivitet> getFromJira(YearMonth yearMonth) throws IOException {
        Konfig konfig = dbService.getKonfig();
        List<DatoAktivitet> aktivitetList = new ArrayList<>();
        for (AktivitetDef aktivitetDef : konfig.aktiviteter) {
            aktivitetList.addAll(jiraIssueService.getWorkLog(aktivitetDef.id, konfig.jiraKontoId, yearMonth));
        }
        return aktivitetList;
    }

    public void push(YearMonth yearMonth, boolean test) throws IOException {
        PushDesc pushDesc = buildPush(yearMonth);
        if (pushDesc.adds.isEmpty() && pushDesc.deletes.isEmpty() && pushDesc.adds.isEmpty()) {
            System.out.println("Lokal timeliste og JIRA worklog er identiske for " + DateUtil.formatYearMonth(yearMonth));
            return;
        }
        if (!pushDesc.deletes.isEmpty()) {
            System.out.println("Følgende aktiviteter vil slettes fra JIRA");
            for (DatoAktivitet aktivitet : pushDesc.deletes) {
                System.out.println(aktivitet.aktivitet + ";" + aktivitet.dato + ";" + aktivitet.timer + " \t#worklog id = " + aktivitet.id);
                if (!test) {
                    jiraIssueService.deleteWorkLogEntry(aktivitet.aktivitet, aktivitet.id);
                }
            }
        }
        if (!pushDesc.adds.isEmpty()) {
            System.out.println("Følgende aktiviteter logges til JIRA");
            for (DatoAktivitet aktivitet : pushDesc.adds) {
                System.out.println(aktivitet.aktivitet + ";" + aktivitet.dato + ";" + aktivitet.timer);
                if (!test) {
                    jiraIssueService.logWork(aktivitet.aktivitet, toOffsetDateTimeAtNineOslo(aktivitet.dato), hoursToDuration(aktivitet.timer));
                }
            }
        }
    }

    public static Duration hoursToDuration(float hours) {
        long minutes = (long) (hours * 60);
        return Duration.ofMinutes(minutes);
    }

    public static OffsetDateTime toOffsetDateTimeAtNineOslo(LocalDate localDate) {
        ZoneId osloZoneId = ZoneId.of("Europe/Oslo");
        LocalDateTime localDateTime = localDate.atTime(9, 0);
        ZonedDateTime zonedDateTime = localDateTime.atZone(osloZoneId);
        return zonedDateTime.toOffsetDateTime();
    }

    private PushDesc buildPush(YearMonth yearMonth) throws IOException {
        Konfig konfig = dbService.getKonfig();
        List<DatoAktivitet> jiraLog = getFromJira(yearMonth);
        List<DatoAktivitet> lokalLog = dbService.load(yearMonth);
        PushDesc pushDesc = getPushDesc(yearMonth, konfig.aktiviteter, lokalLog, jiraLog);
        return pushDesc;
    }

    public void initNextMonth() throws IOException {
        dbService.initNextMonth();
    }
}

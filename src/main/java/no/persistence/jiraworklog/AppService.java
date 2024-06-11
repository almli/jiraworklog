package no.persistence.jiraworklog;

import no.persistence.jiraworklog.model.*;
import no.persistence.jiraworklog.util.DateUtil;

import java.io.IOException;
import java.time.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

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
        Map<DatoAktivitetKey, String> jiraActivityDays = TimelisteIO.toActivityDays(yearMonth, konfig.aktiviteter, jiraLog);
        Map<DatoAktivitetKey, String> lokalActivityDays = TimelisteIO.toActivityDays(yearMonth, konfig.aktiviteter, lokalLog);
        PushDesc pushDesc = new PushDesc();
        pushDesc.deletes = new ArrayList<>();
        pushDesc.adds = new ArrayList<>();
        lokalActivityDays.entrySet().forEach(entry -> {
            if (!jiraActivityDays.containsKey(entry.getKey())) {
                pushDesc.adds.addAll(findDatoAktivitet(lokalLog, entry.getKey()));
            } else if (!entry.getValue().equalsIgnoreCase(jiraActivityDays.get(entry.getKey()))) {
                pushDesc.adds.addAll(findDatoAktivitet(lokalLog, entry.getKey()));
                pushDesc.deletes.addAll(findDatoAktivitet(jiraLog, entry.getKey()));
            }
        });
        jiraActivityDays.entrySet().forEach(entry -> {
            if (!lokalActivityDays.containsKey(entry.getKey())) {
                pushDesc.deletes.addAll(findDatoAktivitet(jiraLog, entry.getKey()));
            }
        });
        pushDesc.adds.sort(Comparator.comparing((DatoAktivitet a) -> a.dato).thenComparing(a -> a.aktivitet));
        pushDesc.deletes.sort(Comparator.comparing((DatoAktivitet a) -> a.dato).thenComparing(a -> a.aktivitet));
        return pushDesc;
    }

    private List<DatoAktivitet> findDatoAktivitet(List<DatoAktivitet> aktivitetList, DatoAktivitetKey key) {
        return aktivitetList.stream().filter(aktivitet -> aktivitet.aktivitet.equalsIgnoreCase(key.aktivitetId) && aktivitet.dato.equals(key.dato)).toList();
    }

    public void initNextMonth() throws IOException {
        dbService.initNextMonth();
    }
}

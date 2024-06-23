package no.persistence.jiraworklog.util;

import no.persistence.jiraworklog.model.AktivitetDef;
import no.persistence.jiraworklog.model.DatoAktivitet;
import no.persistence.jiraworklog.model.DatoAktivitetKey;
import no.persistence.jiraworklog.model.PushDesc;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

import static no.persistence.jiraworklog.util.StringUtil.adjustStringLength;

public class DatoAktivitetUtil {
    public static String findTimer(List<DatoAktivitet> aktiviteter, LocalDate date, String aktivitetId) {
        float timer = 0;
        for (DatoAktivitet aktivitet : aktiviteter) {
            if (aktivitet.dato.equals(date) && aktivitet.aktivitet.equals(aktivitetId)) {
                timer += aktivitet.timer;
            }
        }
        return timer > 0.1 ? String.valueOf(timer) : "";
    }

    public static PushDesc getPushDesc(YearMonth yearMonth, List<AktivitetDef> aktivitetDefList, List<DatoAktivitet> localLog, List<DatoAktivitet> remoteLog) {
        Map<DatoAktivitetKey, String> jiraActivityDays = toActivityDays(yearMonth, aktivitetDefList, remoteLog);
        Map<DatoAktivitetKey, String> lokalActivityDays = toActivityDays(yearMonth, aktivitetDefList, localLog);
        PushDesc pushDesc = new PushDesc();
        pushDesc.deletes = new ArrayList<>();
        pushDesc.adds = new ArrayList<>();
        lokalActivityDays.entrySet().forEach(entry -> {
            if (!jiraActivityDays.containsKey(entry.getKey())) {
                pushDesc.adds.addAll(findDatoAktivitet(localLog, entry.getKey()));
            } else if (!entry.getValue().equalsIgnoreCase(jiraActivityDays.get(entry.getKey()))) {
                pushDesc.adds.addAll(findDatoAktivitet(localLog, entry.getKey()));
                pushDesc.deletes.addAll(findDatoAktivitet(remoteLog, entry.getKey()));
            }
        });
        jiraActivityDays.entrySet().forEach(entry -> {
            if (!lokalActivityDays.containsKey(entry.getKey())) {
                pushDesc.deletes.addAll(findDatoAktivitet(remoteLog, entry.getKey()));
            }
        });
        pushDesc.adds.sort(Comparator.comparing((DatoAktivitet a) -> a.dato).thenComparing(a -> a.aktivitet));
        pushDesc.deletes.sort(Comparator.comparing((DatoAktivitet a) -> a.dato).thenComparing(a -> a.aktivitet));
        return pushDesc;
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

    private static List<DatoAktivitet> findDatoAktivitet(List<DatoAktivitet> aktivitetList, DatoAktivitetKey key) {
        return aktivitetList.stream().filter(aktivitet -> aktivitet.aktivitet.equalsIgnoreCase(key.aktivitetId) && aktivitet.dato.equals(key.dato)).toList();
    }

}

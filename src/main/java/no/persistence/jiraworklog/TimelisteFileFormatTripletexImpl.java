package no.persistence.jiraworklog;

import no.persistence.jiraworklog.model.AktivitetDef;
import no.persistence.jiraworklog.model.DatoAktivitet;
import no.persistence.jiraworklog.model.Konfig;
import no.persistence.jiraworklog.model.TimelisteFileFormat;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class TimelisteFileFormatTripletexImpl implements TimelisteFileFormat {

    @Override
    public List<DatoAktivitet> deserialize(byte[] data, YearMonth month, Konfig konfig) {
        List<DatoAktivitet> list = new ArrayList<>();
        try (Reader reader = new StringReader(new String(data));) {
            CSVFormat format = CSVFormat.DEFAULT.withDelimiter(';');
            Iterable<CSVRecord> records = format.parse(reader);

            for (CSVRecord record : records) {
                // Iterate over all days in the YearMonth
                for (int day = 1; day <= month.lengthOfMonth(); day++) {
                    DatoAktivitet rta = new DatoAktivitet();
                    rta.dato = month.atDay(day);
                    String aktivitet = record.get(5);
                    aktivitet = mapAktivitet(aktivitet, konfig);
                    rta.aktivitet = aktivitet;
                    rta.timer = Float.parseFloat(record.get(5 + day));
                    list.add(rta);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    @Override
    public byte[] serialize(YearMonth yearMonth, List<AktivitetDef> aktivitetDefList, List<DatoAktivitet> aktiviteter) {
        StringWriter sw = new StringWriter();
        String dummyNavn = "Bjørn Haakenstad";
        Map<String, List<DatoAktivitet>> groupedByAktivitet = aktiviteter
                .stream()
                .collect(Collectors.groupingBy(datoAktivitet -> datoAktivitet.aktivitet));
        for (String aktivitet : groupedByAktivitet.keySet()) {
            sw.write(";Avdeling"+";;"+ dummyNavn +";;"+aktivitet+";");
            for (int day = 1; day <= yearMonth.lengthOfMonth(); day++) {
                sw.write(findTimer(groupedByAktivitet.get(aktivitet), yearMonth.atDay(day)) + ";");
            }
            sw.write("\n");
        }
        return sw.toString().getBytes();
    }

    private String mapAktivitet(String aktivitet, Konfig konfig) {
        List<AktivitetDef> configuredAktiviteter = konfig.aktiviteter;
        Optional<AktivitetDef> result = findAktivitetByNavn(configuredAktiviteter, aktivitet);
        if (result.isPresent()) {
            return result.get().id;
        } else {
            return aktivitet;
        }
    }

    public Optional<AktivitetDef> findAktivitetByNavn(List<AktivitetDef> list, String aktivitet) {
        if (list == null) return Optional.empty();
        return list.stream()
                .filter(ad -> ad.navn.equals(aktivitet))
                .findFirst();
    }

    private static String findTimer(List<DatoAktivitet> aktiviteter, LocalDate date) {
        float timer = 0;
        for (DatoAktivitet aktivitet : aktiviteter) {
            if (aktivitet.dato.equals(date)) {
                timer += aktivitet.timer;
            }
        }
        return timer > 0.1 ? String.valueOf(timer) : "0";
    }

    @Override
    public String getFormatName() {
        return "tripletex";
    }

    @Override
    public String getFileType() {
        return "csv";
    }

    @Override
    public String getDescription() {
        return "Format eksportert fra Tripletex. Eksportert uten kolonneheadere og i format UTF-8 med semikolon som skilletegn.";
    }

}

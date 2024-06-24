package no.persistence.jiraworklog.model;

import java.time.YearMonth;
import java.util.List;

public interface TimelisteFileFormat {
    String getFormatName();
    String getFileType();
    String getDescription();
    byte[] serialize(YearMonth yearMonth, List<AktivitetDef> aktivitetDefList, List<DatoAktivitet> aktiviteter);
    List<DatoAktivitet> deserialize(byte[] data, YearMonth month, Konfig konfig);
}

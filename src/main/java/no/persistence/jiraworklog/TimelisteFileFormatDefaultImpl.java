package no.persistence.jiraworklog;

import no.persistence.jiraworklog.model.AktivitetDef;
import no.persistence.jiraworklog.model.DatoAktivitet;
import no.persistence.jiraworklog.model.Konfig;
import no.persistence.jiraworklog.model.TimelisteFileFormat;
import no.persistence.jiraworklog.util.DateInfoUtil;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;
import static no.persistence.jiraworklog.util.DatoAktivitetUtil.findTimer;
import static no.persistence.jiraworklog.util.StringUtil.adjustStringLength;

public class TimelisteFileFormatDefaultImpl implements TimelisteFileFormat {

    public static final String STANDARD = "standard";
    public static final String kolonner = "ukedag;dato;aktivitet;timer;kommentar";

    public List<DatoAktivitet> deserialize(byte[] data, YearMonth month, Konfig konfig) {
        List<DatoAktivitet> list = new ArrayList<>();
        try (Reader reader = new StringReader(new String(data));) {
            CSVFormat format = CSVFormat.DEFAULT.withDelimiter(';');
            Iterable<CSVRecord> records = format.parse(reader);
            boolean isFirstRecord = true;
            for (CSVRecord record : records) {
                if (isFirstRecord) {
                    isFirstRecord = false;
                    continue; // skip header
                }
                if (isNullOrEmpty(record.get(3))) {
                    continue;
                }
                DatoAktivitet rta = new DatoAktivitet();
                rta.dato = LocalDate.parse(record.get(1));
                rta.aktivitet = record.get(2);
                rta.timer = Float.parseFloat(record.get(3));
                rta.kommentar = record.get(4);
                list.add(rta);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public byte[] serialize(YearMonth yearMonth, List<AktivitetDef> aktivitetDefList, List<DatoAktivitet> aktiviteter) {
        StringWriter sw = new StringWriter();
        sw.write(kolonner + "\n");
        LocalDate date = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        do {
            for (AktivitetDef aktivitet : aktivitetDefList) {
                sw.write(adjustStringLength(DateInfoUtil.getDateInfo(date), 7) + ";" + date + ";" + aktivitet.id + ";" + findTimer(aktiviteter, date, aktivitet.id) + ";;\n");
            }
            date = date.plusDays(1);
        } while (!date.isAfter(endDate));
        return sw.toString().getBytes();
    }

    @Override
    public String getFileType() {
        return "csv";
    }

    @Override
    public String getDescription() {
        return "Default filformat for timelister. CSV med kolonnene "+kolonner;
    }

    @Override
    public String getFormatName() {
        return STANDARD;
    }
}

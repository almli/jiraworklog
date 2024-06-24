package no.persistence.jiraworklog;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import no.persistence.jiraworklog.model.AktivitetDef;
import no.persistence.jiraworklog.model.DatoAktivitet;
import no.persistence.jiraworklog.model.TimelisteFileFormat;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TimelisteFileFormatJsonImpl implements TimelisteFileFormat {

    public List<DatoAktivitet> deserialize(byte[] data) {
        return fromJsonFileBytes(data);
    }

    public byte[] serialize(YearMonth yearMonth, List<AktivitetDef> aktivitetDefList, List<DatoAktivitet> aktiviteter) {
        return toJsonFileBytes(aktiviteter);
    }

    private byte[] toJsonFileBytes(List<DatoAktivitet> aktiviteter) {
        return getGson().toJson(new Timeliste(aktiviteter)).getBytes();
    }

    private List<DatoAktivitet> fromJsonFileBytes(byte[] fileData) {
        return getGson().fromJson(new String(fileData), Timeliste.class).aktiviteter;
    }

    @Override
    public String getFileType() {
        return "json";
    }

    @Override
    public String getDescription() {
        return "Json timeliste filformat";
    }

    @Override
    public String getFormatName() {
        return "json";
    }

    private static class Timeliste {
        public List<DatoAktivitet> aktiviteter;

        public Timeliste() {
        }

        public Timeliste(List<DatoAktivitet> aktiviteter) {
            this.aktiviteter = aktiviteter;
        }
    }

    private Gson getGson() {
        return new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();
    }

    private static class LocalDateAdapter extends TypeAdapter<LocalDate> {

        private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

        @Override
        public void write(JsonWriter out, LocalDate value) throws IOException {
            if (value != null) {
                out.value(value.format(formatter));
            } else {
                out.nullValue();
            }
        }

        @Override
        public LocalDate read(JsonReader in) throws IOException {
            if (in != null) {
                return LocalDate.parse(in.nextString(), formatter);
            } else {
                return null;
            }
        }
    }
}

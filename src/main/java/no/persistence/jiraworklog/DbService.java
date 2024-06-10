package no.persistence.jiraworklog;

import no.persistence.jiraworklog.model.DatoAktivitet;
import no.persistence.jiraworklog.model.Konfig;
import no.persistence.jiraworklog.util.DateUtil;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;
import java.util.OptionalInt;

public class DbService {

    private final String rotkatalog;

    public DbService(String rotkatalog) {
        this.rotkatalog = rotkatalog;
    }

    public Konfig getKonfig() {
        return read(rotkatalog + "/konfig.yaml", Konfig.class);
    }

    private static <T> T read(String path, Class<T> t) {
        Yaml yaml = new Yaml();
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(path);
            T val = yaml.loadAs(fileInputStream, t);
            fileInputStream.close();
            return val;
        } catch (IOException e) {
            throw new RuntimeException("Kunne ikke lese " + path, e);
        }
    }

    public YearMonth getCurrentMonth() {
        File file = new File(rotkatalog);
        OptionalInt siste = Arrays.stream(file.listFiles(f -> !f.isDirectory() && FileNameUtil.isTimlelisteFileName(f.getName()))).mapToInt(f -> Integer.parseInt(DateUtil.formatYearMonth(FileNameUtil.getYearMonthFromFileName(f.getName())))).max();
        return siste.isEmpty() ? null : DateUtil.parseYearMonth(siste.getAsInt() + "");
    }

    public YearMonth initNextMonth() throws IOException {
        YearMonth yearMonth = getCurrentMonth();
        if (yearMonth == null) {
            yearMonth = YearMonth.now();
        } else {
            yearMonth = yearMonth.plusMonths(1);
        }
        initMonth(yearMonth);
        return yearMonth;
    }

    private void initMonth(YearMonth yearMonth) throws IOException {
        String timeliste = TimelisteIO.writeToString(yearMonth, getKonfig().aktiviteter, List.of());
        Files.writeString(Paths.get(timelisteFilename("", yearMonth)), timeliste);
    }

    public void save(YearMonth yearMonth, List<DatoAktivitet> aktivitetList, String suffix) throws IOException {
        String timeliste = TimelisteIO.writeToString(yearMonth, getKonfig().aktiviteter, aktivitetList);
        Files.writeString(Paths.get(timelisteFilename(suffix, yearMonth)), timeliste);
    }

    private String timelisteFilename(String suffix, YearMonth yearMonth) {
        return rotkatalog + FileNameUtil.toTimelisteFileName(suffix, yearMonth);
    }

    public List<DatoAktivitet> load(YearMonth yearMonth) throws IOException {
        Path path = Paths.get(timelisteFilename("", yearMonth));
        if (!Files.exists(path)) {
            initMonth(yearMonth);
        }

        return TimelisteIO.readFromString(Files.readString(path));
    }

}

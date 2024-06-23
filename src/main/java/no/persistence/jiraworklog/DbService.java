package no.persistence.jiraworklog;

import no.persistence.jiraworklog.model.DatoAktivitet;
import no.persistence.jiraworklog.model.Konfig;
import no.persistence.jiraworklog.model.TimelisteFileFormat;
import no.persistence.jiraworklog.util.DateUtil;
import no.persistence.jiraworklog.util.FileNameUtil;
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

    private TimelisteFileFormat _filformatImpl;

    public DbService(String rotkatalog) {
        if (rotkatalog != null && !rotkatalog.endsWith("/") && !rotkatalog.endsWith("\\")) {
            rotkatalog = rotkatalog + "/";
        }
        this.rotkatalog = rotkatalog;
        if (!Files.exists(Paths.get(rotkatalog))) {
            throw new RuntimeException("Katalogen " + rotkatalog + " finnes ikke. Opprett katalogen og legg til konfigurasjonsfilen 'konfig.yaml'. Se README.md for mer informasjon.");
        }
        getKonfig();
    }

    public Konfig getKonfig() {
        return read(rotkatalog + "/konfig.yaml", Konfig.class);
    }

    private TimelisteFileFormat getTimelisteFilformat() {
        if (_filformatImpl == null) {
            _filformatImpl = TimelisteFileFormatImplRepo.getByName(getKonfig().timelisteformat);
        }
        return _filformatImpl;
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
        String filtype = getTimelisteFilformat().getFileType();
        OptionalInt siste = Arrays.stream(file.listFiles(f -> !f.isDirectory() && FileNameUtil.isTimlelisteFileName(f.getName(), filtype))).mapToInt(f -> Integer.parseInt(DateUtil.formatYearMonth(FileNameUtil.getYearMonthFromFileName(f.getName(), filtype)))).max();
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
        byte[] data = getTimelisteFilformat().serialize(yearMonth, getKonfig().aktiviteter, List.of());
        Files.write(Paths.get(timelisteFilename("", yearMonth)), data);
    }

    public void save(YearMonth yearMonth, List<DatoAktivitet> aktivitetList, String suffix) throws IOException {
        byte[] timeliste = getTimelisteFilformat().serialize(yearMonth, getKonfig().aktiviteter, aktivitetList);
        Files.write(Paths.get(timelisteFilename(suffix, yearMonth)), timeliste);
    }

    private String timelisteFilename(String suffix, YearMonth yearMonth) {
        return rotkatalog + FileNameUtil.toTimelisteFileName(suffix, yearMonth, getTimelisteFilformat().getFileType());
    }

    public List<DatoAktivitet> load(YearMonth yearMonth) throws IOException {
        Path path = Paths.get(timelisteFilename("", yearMonth));
        if (!Files.exists(path)) {
            initMonth(yearMonth);
        }
        return getTimelisteFilformat().deserialize(Files.readAllBytes(path));
    }
}

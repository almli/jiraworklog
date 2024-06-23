package no.persistence.jiraworklog;

import no.persistence.jiraworklog.model.AktivitetDef;
import no.persistence.jiraworklog.model.DatoAktivitet;
import no.persistence.jiraworklog.model.PushDesc;
import no.persistence.jiraworklog.model.TimelisteFileFormat;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import static no.persistence.jiraworklog.util.DatoAktivitetUtil.getPushDesc;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class FileFormatTest {

    @Test
    void testAllFormats() {
        YearMonth yearMonth = YearMonth.of(2024, 6);
        List<DatoAktivitet> aktivitetList = testAktiviteter();
        List<AktivitetDef> defList = testDef();
        for (TimelisteFileFormat impl : TimelisteFileFormatImplRepo.getSupportedTimelisteFormats().values()) {
            System.out.println("Tester " + impl.getFormatName() + " -" + impl.getDescription());
            assertNotNull(impl.getFormatName(), "FormatName mangler");
            assertNotNull(impl.getDescription(), "Description mangler for filformat " + impl.getFormatName());
            assertNotNull(impl.getFileType(), "FileType mangler for filformat " + impl.getFormatName());
            byte[] data = impl.serialize(yearMonth, defList, aktivitetList);
            List<DatoAktivitet> aktivitetDeserilalizedList = impl.deserialize(data);
            PushDesc pushDesc = getPushDesc(yearMonth, defList, aktivitetList, aktivitetDeserilalizedList);
            assertEquals(0, pushDesc.adds.size(), "Etter lagring har det dukket opp " + pushDesc.adds.size() + " aktiviteter : " + pushDesc.adds + ", for filformat " + impl.getFormatName());
            assertEquals(0, pushDesc.deletes.size(), "Etter lagring har blr " + pushDesc.adds.size() + " aktivitete mistet : " + pushDesc.deletes + ", for filformat " + impl.getFormatName());
        }
    }

    private List<AktivitetDef> testDef() {
        List<AktivitetDef> defs = new ArrayList<>();
        defs.add(def("A1"));
        defs.add(def("A2"));
        defs.add(def("A3"));
        return defs;
    }

    private AktivitetDef def(String id) {
        AktivitetDef def = new AktivitetDef();
        def.id = id;
        def.navn = "navn-" + id;
        return def;
    }

    private static List<DatoAktivitet> testAktiviteter() {
        List<DatoAktivitet> aktiviteter = new ArrayList<>();
        aktiviteter.add(datoAktivitet(LocalDate.of(2024, 6, 1), "A1", 1.5f));
        aktiviteter.add(datoAktivitet(LocalDate.of(2024, 6, 1), "A2", 6f));
        aktiviteter.add(datoAktivitet(LocalDate.of(2024, 6, 4), "A1", 7.5f));
        aktiviteter.add(datoAktivitet(LocalDate.of(2024, 6, 5), "A1", 7.5f));
        aktiviteter.add(datoAktivitet(LocalDate.of(2024, 6, 6), "A1", 7.5f));
        aktiviteter.add(datoAktivitet(LocalDate.of(2024, 6, 7), "A1", 7.5f));
        return aktiviteter;
    }

    private static DatoAktivitet datoAktivitet(LocalDate dato, String aktivitet, float timer) {
        DatoAktivitet da = new DatoAktivitet();
        da.timer = timer;
        da.aktivitet = aktivitet;
        da.dato = dato;
        return da;
    }
}

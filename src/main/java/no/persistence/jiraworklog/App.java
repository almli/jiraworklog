package no.persistence.jiraworklog;

import no.persistence.jiraworklog.model.Konfig;
import no.persistence.jiraworklog.model.TimelisteFileFormat;
import no.persistence.jiraworklog.util.DateUtil;

import java.io.IOException;
import java.io.InputStream;
import java.time.YearMonth;
import java.util.List;
import java.util.Properties;

public class App {

    public static void main(String[] args) throws IOException {
        System.out.println("jiraworklog versjon " + getVersjon() + " args:" + List.of(args));
        if (args.length > 3 || args.length < 2) {
            printHelp();
            return;
        }
        DbService dbService = new DbService(args[0]);
        YearMonth yearMonth = null;
        if (args.length == 3) {
            yearMonth = DateUtil.parseYearMonth(args[2]);
        } else {
            yearMonth = dbService.getCurrentMonth();
        }
        if (yearMonth == null) {
            yearMonth = YearMonth.now();
        }
        Konfig konfig = dbService.getKonfig();
        JiraIssueService jiraIssueService = new JiraIssueService(konfig.jiraUrl, konfig.jiraApiToken, konfig.jiraKontoId);
        AppService appService = new AppService(dbService, jiraIssueService);
        String kommando = args[1];
        if ("next".equalsIgnoreCase(kommando)) {
            appService.initNextMonth();
        } else if ("pull".equalsIgnoreCase(kommando)) {
            appService.pull(yearMonth);
        } else if ("test".equalsIgnoreCase(kommando)) {
            appService.push(yearMonth, true);
        } else if ("push".equalsIgnoreCase(kommando)) {
            appService.push(yearMonth, false);
        } else {
            System.out.println("Kommando må være 'next', 'pull', 'test' eller 'push'");
        }
    }

    public static String getVersjon() {
        Properties properties = new Properties();
        // Use ClassLoader to access resources within the JAR
        try (InputStream inputStream = App.class.getClassLoader().getResourceAsStream(
                "META-INF/maven/no.persistence.jiraworklog/jiraworklog/pom.properties")) {
            if (inputStream != null) {
                properties.load(inputStream);
                return properties.getProperty("version", "Version not found");
            } else {
                return "";
            }
        } catch (IOException e) {
            return "Error reading version: " + e.getMessage();
        }
    }

    private static void printHelp() {
        System.out.println("Dette programmet krever to eller tre parametere:");
        System.out.println("1) rotkatalog - Katalogen som inneholder konfigurasjonsfilen 'konfig.yaml' og timelistene.");
        System.out.println("2) kommando - Kan være ett av følgende:");
        System.out.println("   a) next - Initialiserer timelisten for neste måned.");
        System.out.println("   b) pull - Henter timelisten fra Jira. Resultatet lagres som timeliste_<yyyyMM>_pull.csv.");
        System.out.println("   c) test - Skriver ut hva som vil bli gjort ved push.");
        System.out.println("   d) push - Oppdaterer Jira med timelisten.");
        System.out.println("3) år måned (valgfritt) - År og måned i formatet 'yyyyMM'. Om ikke oppgitt brukes siste tilgjengelige ('pull', 'test', og 'push').");
        System.out.println("\nFølgende filformater er støttet for timelister:");
        for (TimelisteFileFormat impl : TimelisteFileFormatImplRepo.getSupportedTimelisteFormats().values()) {
            System.out.println(impl.getFormatName() + " : " + impl.getDescription());
        }
        System.out.println("Uavhengig av hvilket format som benyttes så må timelistefilene ligge i data-katalogen med navn timeliste_<yyyyMM>.<filtype>. F.eks timeliste_202406.csv");
    }
}

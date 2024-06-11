package no.persistence.jiraworklog;

import no.persistence.jiraworklog.model.DatoAktivitet;
import no.persistence.jiraworklog.model.Konfig;
import no.persistence.jiraworklog.util.DateUtil;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.YearMonth;
import java.util.List;

class AppServiceTest {
    @Disabled
    void testPull() throws IOException {
        DbService dbService = new DbService("data/");
        Konfig konfig = dbService.getKonfig();
        JiraIssueService jiraIssueService = new JiraIssueService(konfig.jiraUrl, konfig.jiraApiToken, konfig.jiraKontoId);
        AppService appService = new AppService(dbService, jiraIssueService);
        appService.pull(DateUtil.parseYearMonth("202406"));
    }

    @Disabled
    void testTest() throws IOException {
        DbService dbService = new DbService("data/");
        Konfig konfig = dbService.getKonfig();
        JiraIssueService jiraIssueService = new JiraIssueService(konfig.jiraUrl, konfig.jiraApiToken, konfig.jiraKontoId);
        AppService appService = new AppService(dbService, jiraIssueService);
        appService.push(DateUtil.parseYearMonth("202406"), true);
    }

    @Disabled
    void test2() throws IOException {
        DbService dbService = new DbService("data/");
        YearMonth yearMonth = DateUtil.parseYearMonth("202406");
        List<DatoAktivitet> aktList = dbService.load(yearMonth);
        dbService.save(yearMonth, aktList, "_db");
    }
}
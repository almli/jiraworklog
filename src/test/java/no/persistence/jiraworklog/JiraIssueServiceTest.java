package no.persistence.jiraworklog;

import no.persistence.jiraworklog.model.DatoAktivitet;
import no.persistence.jiraworklog.util.DateUtil;
import org.junit.jupiter.api.Disabled;

import java.io.IOException;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;

class JiraIssueServiceTest {

    private static final String JIRA_URL = "https://sb1forsikring.atlassian.net";
    private static final String token = "";
    private static final String account = "lars.ivar.almli@sparebank1.no";

    @Disabled
    void get() throws IOException {
        JiraIssueService jiraIssueService = new JiraIssueService(JIRA_URL, token, account);
        List<DatoAktivitet> log = jiraIssueService.getWorkLog("PIP-6379", "lars.ivar.almli@sparebank1.no", DateUtil.parseYearMonth("202406"));
        System.out.println(log);
    }

    @Disabled
    void log() throws IOException {
        JiraIssueService jiraIssueService = new JiraIssueService(JIRA_URL, token, account);
        jiraIssueService.logWork("PIP-6379", OffsetDateTime.now().minusHours(10), Duration.ofHours(1));
    }

    @Disabled
    void delete() throws IOException {
        JiraIssueService jiraIssueService = new JiraIssueService(JIRA_URL, token, account);
        jiraIssueService.deleteWorkLogEntry("PIP-6379", "22972");
    }
}
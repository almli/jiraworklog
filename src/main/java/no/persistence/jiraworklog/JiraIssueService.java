package no.persistence.jiraworklog;

import no.persistence.jiraworklog.model.DatoAktivitet;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class JiraIssueService {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    private final String jiraBaseUri;
    private final String jiraToken;
    private final String jiraAccountId;

    public JiraIssueService(String jiraBaseUri, String jiraToken, String jiraAccountId) {
        this.jiraBaseUri = jiraBaseUri;
        this.jiraToken = jiraToken;
        this.jiraAccountId = jiraAccountId;
    }

    private String getAuthHeader() throws IOException {
        if (jiraToken == null || jiraAccountId == null) {
            throw new IOException("Jira token or account id not set");
        }
        String credentials = Base64.getEncoder().encodeToString((jiraAccountId + ":" + jiraToken).getBytes());
        return "Basic " + credentials;
    }

    public void deleteWorkLogEntry(String issueId, String worklogId) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(jiraBaseUri + "/rest/api/3/issue/" + issueId + "/worklog/" + worklogId)
                .delete()
                .addHeader("Authorization", getAuthHeader())
                .addHeader("Content-Type", "application/json")
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            System.out.println("Worklog entry deleted successfully.");
        }
    }

    public String logWork(String issueId, OffsetDateTime startet, Duration duration) throws IOException, JSONException {
        OkHttpClient client = new OkHttpClient();
        JSONObject payload = new JSONObject();
        payload.put("started", startet.format(dateTimeFormatter));
        payload.put("timeSpentSeconds", duration.getSeconds());
        RequestBody body = RequestBody.create(payload.toString(), MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(jiraBaseUri + "/rest/api/3/issue/" + issueId + "/worklog")
                .post(body)
                .addHeader("Authorization", getAuthHeader())
                .addHeader("Content-Type", "application/json")
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            String responseBody = response.body().string();
            JSONObject jsonResponse = new JSONObject(responseBody);
            return jsonResponse.getString("id");
        }
    }

    public List<DatoAktivitet> getWorkLog(String issueId, String personId, YearMonth yearMonth) throws IOException, JSONException {
        LocalDateTime startOfMonth = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = yearMonth.atEndOfMonth().atTime(LocalTime.MAX);
        long startedAfter = startOfMonth.toInstant(ZoneOffset.UTC).toEpochMilli();
        long startedBefore = endOfMonth.toInstant(ZoneOffset.UTC).toEpochMilli();
        OkHttpClient client = new OkHttpClient();
        HttpUrl url = HttpUrl.parse(jiraBaseUri + "/rest/api/3/issue/" + issueId + "/worklog").newBuilder()
                .addQueryParameter("startedAfter", String.valueOf(startedAfter))
                .addQueryParameter("startedBefore", String.valueOf(startedBefore))
                .build();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("Authorization", getAuthHeader())
                .addHeader("Accept", "application/json")
                .build();
        List<DatoAktivitet> workLogs = new ArrayList<>();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            String responseBody = response.body().string();
            JSONObject jsonResponse = new JSONObject(responseBody);
            JSONArray worklogs = jsonResponse.getJSONArray("worklogs");
            for (int i = 0; i < worklogs.length(); i++) {
                JSONObject worklog = worklogs.getJSONObject(i);
                JSONObject author = worklog.getJSONObject("author");
                String authorEmail = author.getString("emailAddress");
                if (!personId.equalsIgnoreCase(authorEmail)) {
                    continue;
                }
                DatoAktivitet aktivitet = new DatoAktivitet();
                LocalDateTime started = LocalDateTime.parse(worklog.getString("started"), dateTimeFormatter);
                aktivitet.dato = started.toLocalDate();
                JSONObject comment = worklog.optJSONObject("comment");
                if (comment != null) {
                    // aktivitet.kommentar = comment.getJSONArray("content").getJSONObject(0).getJSONArray("content").getJSONObject(0).getString("text");
                }
                aktivitet.id = worklog.getString("id");
                aktivitet.timer = worklog.getInt("timeSpentSeconds") / 3600.0f;
                aktivitet.aktivitet = issueId;
                workLogs.add(aktivitet);
            }
        }
        return workLogs;
    }
}

# jiraworklog

Bygges med `mvn clean package`

Kjøres med `./worklog`

Katalogen som oppgies i worklog scriptet må inneholde en konfigurasjonsfilen beskrevet under.

## Miljøvariabel

Du kan sette miljøvariabelen `JIRAWORKLOG_HOME` for å spesifisere katalogen som inneholder konfigurasjonsfilen og
timelistene. Hvis `JIRAWORKLOG_HOME` ikke er satt eller er tom, vil standardkatalogen `./data/` bli brukt.

Eksempel:

```sh
export JIRAWORKLOG_HOME="/path/to/your/config"
./worklog
konfig.yaml:
```yaml
jiraKontoId: <din epost adresse>
jiraApiToken: <api token fra https://id.atlassian.com/manage-profile/security/api-tokens >
jiraUrl: https://<domene>.atlassian.net

# jira saker det skal føres timer på
aktiviteter:
  - id: TIM-1
    navn: Fremtidens saksbehandling
  - id: TIM-6
    navn: PS27
  - id: TIM-11
    navn: Utvikling - Nyutvikling
  - id: TIM-12
    navn: Utvikling - Forvaltning

```


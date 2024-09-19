# jiraworklog

[jiraworklog](https://github.com/almli/jiraworklog), et program som lar deg føre timer i Jira via en lokal CSV-fil.

## Komme i gang

1. Legg inn din informasjon i filen `data/konfig.yaml`.
2. Kjør de nødvendige kommandoene som beskrevet nedenfor.


data/konfig.yaml:
```yaml
jiraKontoId: <din epost adresse>
jiraApiToken: <api token fra https://id.atlassian.com/manage-profile/security/api-tokens >
jiraUrl: https://<domene>.atlassian.net
timelisteformat: standard

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

Hvordan du bruker dette verktøyet vil være litt avhengig av hva som er din master for timeføring:

Hvis du
- hovedsakling fører timer her:
  - kjør next
  - før timer i regnearker som lages av next
  - (kjør test hvis du ønsker å sjekke hva som skrives til jira)
  - push
  - (evt gjenta syklusen før timer -> push hvius du fører timer flere ganger i månedene)
- har ført timer direkte i jira:
  - kjør pull
  - kopier over til fil på navneformat timeliste_YYYYMM.csv
  - fortsett med flyten over
- fører timer i ekstern system, feks Tripletex:
  - eksporter timeliste fra det eksterne systemet, feks Tripletex til csv. Kall filen for timeliste_YYYYMM.csv og legg den på samme sted som konfig.yml
  - (kjør test hvis du ønsker å sjekke hva som skrives til jira)
  - push



## Kommandoer

### På Windows:

- `next.cmd`
  - Genererer en ny fil i `data/` med navn `timeliste_<yyyyMM>.csv` med dine aktiviteter for hver dag i måneden.

- `pull.cmd`
  - Henter data fra Jira og skriver til `data/timeliste_<yyyyMM>_pull.csv`.
  - Fjern `_pull` fra filnavnet hvis du ønsker å bruke denne filen til føring av timer.

- `test.cmd`
  - Skriver ut hva som vil bli gjort ved `push.cmd`.

- `push.cmd`
  - Sender data til Jira fra `data/timeliste_<yyyyMM>.csv`.

Du kan også spesifisere hvilken måned du vil jobbe med ved å sende med `yyyyMM` som argument til skriptene, for eksempel `pull.cmd 202406`.

### På Linux:

- `./worklog next`
  - Genererer en ny fil i `data/` med navn `timeliste_<yyyyMM>.csv` med dine aktiviteter for hver dag i måneden.

- `./worklog pull`
  - Henter data fra Jira og skriver til `data/timeliste_<yyyyMM>_pull.csv`.
  - Fjern `_pull` fra filnavnet hvis du ønsker å bruke denne filen til føring av timer.

- `./worklog test`
  - Skriver ut hva som vil bli gjort ved `push`.

- `./worklog push`
  - Sender data til Jira fra `data/timeliste_<yyyyMM>.csv`.

Du kan sette miljøvariabelen `JIRAWORKLOG_HOME` for å spesifisere katalogen som inneholder konfigurasjonsfilen og
timelistene. Hvis `JIRAWORKLOG_HOME` ikke er satt eller er tom, vil standardkatalogen `./data/` bli brukt.

Eksempel:

```sh
export JIRAWORKLOG_HOME="/path/to/your/config"

./worklog
```

# Utvikling

Bygges med `mvn clean package`

For å legge til støtte for et nytt timeliste-filformat. Implementer TimelisteFileFormat, og kjør testen FileFormatTest.
Alle klasser som implementerer TimelisteFileFormat og som ligger ett sted under no.persistence.jiraworklog vil
automatisk blir tilgjengelig i appen, og aktiveres via timelisteformat verdien i konfig.yaml


# jiraworklog

Dette er en distribusjon av [jiraworklog](https://github.com/almli/jiraworklog), et program som lar deg føre timer i Jira via en lokal CSV-fil.

## Komme i gang

1. Legg inn din informasjon i filen `data/konfig.yaml`.
2. Kjør de nødvendige kommandoene som beskrevet nedenfor.

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


# jiraworklog

Bygges med `mvn clean package`

Se readme i relase pakken for mer informasjon.

For å legge til støtte for et nytt timeliste-filformat. Implementer TimelisteFileFormat, og kjør testen FileFormatTest.
Alle klasser som implementerer TimelisteFileFormat og som ligger ett sted under no.persistence.jiraworklog vil
automatisk blir tilgjengelig i appen, og aktiveres via timelisteformat verdien i konfig.yaml

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



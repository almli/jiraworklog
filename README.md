# jiraworklog

Bygges med `mvn clean package`

Se readme i relase pakken for mer informasjon.

For å legge til støtte for et nytt timeliste-filformat. Implementer TimelisteFileFormat, og kjør testen FileFormatTest.
Alle klasser som implementerer TimelisteFileFormat og som ligger ett sted under no.persistence.jiraworklog vil
automatisk blir tilgjengelig i appen, og aktiveres via timelisteformat verdien i konfig.yaml



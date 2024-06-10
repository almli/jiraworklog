# jiraworklog

bygges med `mvn clean package`

kjøres med `./worklog

katalogen som oppgies i worklog scriptet må inneholde en konfigurasjonsfilen beskrevet under.

konfig.yaml
```yaml
jiraKontoId: <din epost adresse>
jiraApiToken: <api token fra https://id.atlassian.com/manage-profile/security/api-tokens >
jiraUrl: https://sb1forsikring.atlassian.net

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


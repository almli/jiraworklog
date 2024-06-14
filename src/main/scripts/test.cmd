@echo off
setlocal

if "%JIRAWORKLOG_HOME%"=="" (
    set JIRAWORKLOG_HOME=.\data\
)

java -jar lib\jiraworklog.jar "%JIRAWORKLOG_HOME%" test %*
endlocal

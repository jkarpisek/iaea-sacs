@echo off
javaw.exe
if %errorlevel% == 1 (
  start javaw.exe -jar iaea-questionnaire-1.0.jar
) else (
  start java/bin/javaw.exe -jar iaea-questionnaire-1.0.jar
)
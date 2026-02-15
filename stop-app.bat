@echo off
setlocal
set "TITLE=KapilTraders"
set "JAR=kapil-traders-1.0.0.jar"

echo Stopping %TITLE%...

rem Method 1: Kill by Window Title
taskkill /F /FI "WINDOWTITLE eq %TITLE%*" /T >nul 2>&1

rem Method 2: Kill by Command Line (WMIC)
for /f "tokens=2 delims=," %%a in ('wmic process where "name='java.exe' and commandline like '%%%JAR%%%'" get processid /format:csv ^| findstr /r [0-9]') do (
    taskkill /F /PID %%a >nul 2>&1
)

echo %TITLE% stopped.
timeout /t 3

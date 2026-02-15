@echo off
setlocal
set "TITLE=KapilTraders"
set "JAR=target\kapil-traders-1.0.0.jar"

pushd "%~dp0"

echo [1/3] Cleaning up old instances...
taskkill /F /FI "WINDOWTITLE eq %TITLE%*" /T >nul 2>&1

echo [2/3] Checking environment...
if not exist "%JAR%" (
    echo JAR not found. Building...
    call .\mvnw.cmd -DskipTests package
)

echo [3/3] Starting %TITLE%...
rem Using 'cmd /c' to run the process and 'title' to tag the window
start "%TITLE%" cmd /c "title %TITLE% && java -jar %JAR% --spring.profiles.active=local || pause"

echo Waiting for app to start...
ping 127.0.0.1 -n 8 >nul

echo Opening browser...
start "" "http://localhost:8080"

echo Done. You can close this window, but KEEP the "%TITLE%" window open.
timeout /t 5
popd

@echo off
rem Run KapilTraders with the local H2 profile. Double-click to start.
pushd %~dp0
rem Build JAR if missing
if not exist "target\kapil-traders-1.0.0.jar" (
  echo JAR not found, building...
  .\mvnw.cmd -DskipTests package
)
rem Start the app in its own window
start "KapilTraders" cmd /c "cd /d "%~dp0" && java -jar ""%~dp0target\kapil-traders-1.0.0.jar"" --spring.profiles.active=local"
rem Give the app a couple seconds to start, then open the browser
timeout /t 4 >nul
start "" "http://localhost:8080"
popd

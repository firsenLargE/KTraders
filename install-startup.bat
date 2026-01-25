@echo off
rem Install a simple startup script (copies run-local.bat to the user's Startup folder)
set SRC=%~dp0run-local.bat
set DST=%APPDATA%\Microsoft\Windows\Start Menu\Programs\Startup\run-kapiltraders.bat
copy "%SRC%" "%DST%" /Y >nul
if %ERRORLEVEL% EQU 0 (
  echo Startup entry created at %DST%
) else (
  echo Failed to create startup entry. Try running this script as your user.
)
pause

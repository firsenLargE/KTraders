$WshShell = New-Object -comObject WScript.Shell
$DesktopPath = [Environment]::GetFolderPath("Desktop")

# Shortcut for Start
$ShortcutStart = $WshShell.CreateShortcut("$DesktopPath\Start KapilTraders.lnk")
$ShortcutStart.TargetPath = "$PSScriptRoot\start-app.bat"
$ShortcutStart.WorkingDirectory = "$PSScriptRoot"
$ShortcutStart.Description = "Start KapilTraders Application"
$ShortcutStart.IconLocation = "shell32.dll,137" # Run icon
$ShortcutStart.Save()

# Shortcut for Stop
$ShortcutStop = $WshShell.CreateShortcut("$DesktopPath\Stop KapilTraders.lnk")
$ShortcutStop.TargetPath = "$PSScriptRoot\stop-app.bat"
$ShortcutStop.WorkingDirectory = "$PSScriptRoot"
$ShortcutStop.Description = "Stop KapilTraders Application"
$ShortcutStop.IconLocation = "shell32.dll,27" # Stop/X icon
$ShortcutStop.Save()

Write-Host "Shortcuts created on Desktop."

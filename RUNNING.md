# Running KapilTraders (local, easy setup)

This project includes a local H2 profile and helper scripts so anyone can run it on this laptop with zero setup.

Quick start (double-click):
1. Double-click `scripts\run-local.bat` in the project root. This builds the jar (if missing), starts the app with an embedded H2 file DB (profile `local`), and opens http://localhost:8080 in the browser.

Tip: A Desktop launcher `Launch KapilTraders.lnk` has been created on the current user's Desktop — you can use it to start the app without opening folders. If you prefer an icon, run `scripts\create-launch-shortcut.bat` to create a shortcut named **Launch KapilTraders.lnk** with a visible icon and set it to run minimized.

Auto-start on login:
1. Double-click `scripts\install-autostart-fixed.bat` to create a user scheduled task that runs `run-local.bat` at logon and to create a Desktop shortcut called "Open KapilTraders".

Daily backups:
1. Double-click `scripts\install-backup-task.bat` to create a scheduled daily backup at 03:00 that archives the H2 data file and uploads. Backups are stored in `backups/`.

Monitoring:
- The script `scripts\check-and-restart.ps1` is provided. You can create a scheduled task to run it every 5 minutes to auto-restart the app if it crashes.

Run as Windows service (recommended)
- To run KapilTraders as a Windows service (so it starts on boot and doesn't depend on any open terminal), use the NSSM helper scripts:
  - Double-click `scripts\install-nssm-service.bat` **as Administrator** to download NSSM, install the service, configure logs and start it automatically.
  - Check service status with `scripts\service-status.bat` or `sc query KapilTraders`.
  - To uninstall the service, run `scripts\uninstall-nssm-service.bat` as Administrator.

Uploads and storage:
- Uploaded images are stored in `data/uploads/YYYY/MM/` and thumbnails are generated in the same folder with the prefix `thumb_`.
- The application serves images from `http://localhost:8080/uploads/YYYY/MM/<filename>`.

Notes:
- For consistent backup, stop the app before running backups. The automated backup tries to copy files while running but stopping the app is safest.
- The admin user is created on first run: `admin@kapiltraders.com / admin123`.

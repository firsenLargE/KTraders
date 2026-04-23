package com.example.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
public class BackupService {

    @Value("${spring.datasource.url:}")
    private String dbUrl;

    @Value("${spring.datasource.username:}")
    private String dbUsername;

    @Value("${spring.datasource.password:}")
    private String dbPassword;

    @Value("${backup.directory:./backups}")
    private String backupDirectory;

    private static final int RETENTION_DAYS = 30;

    /**
     * Automated daily backup at 2:00 AM
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void scheduledBackup() {
        try {
            System.out.println("Starting scheduled backup at " + LocalDateTime.now());
            createBackup();
            cleanOldBackups();
        } catch (Exception e) {
            System.err.println("Scheduled backup failed: " + e.getMessage());
        }
    }

    /**
     * Manual backup trigger
     */
    public String createBackup() throws IOException, InterruptedException {
        // Ensure backup directory exists
        Path backupPath = Paths.get(backupDirectory);
        if (!Files.exists(backupPath)) {
            Files.createDirectories(backupPath);
        }

        // Generate filename with timestamp
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String filename = "ktraders_backup_" + timestamp + ".sql";
        String fullPath = Paths.get(backupDirectory, filename).toString();

        // Extract database name from JDBC URL
        String dbName = extractDatabaseName(dbUrl);

        // Build pg_dump command
        ProcessBuilder pb = new ProcessBuilder(
                "pg_dump",
                "-h", "localhost",
                "-U", dbUsername,
                "-d", dbName,
                "-f", fullPath,
                "--no-password");

        // Set PGPASSWORD environment variable
        pb.environment().put("PGPASSWORD", dbPassword);
        pb.redirectErrorStream(true);

        Process process = pb.start();
        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new IOException("pg_dump failed with exit code: " + exitCode);
        }

        System.out.println("Backup created successfully: " + filename);
        return filename;
    }

    /**
     * Get list of all backup files
     */
    public List<BackupInfo> getBackupHistory() throws IOException {
        List<BackupInfo> backups = new ArrayList<>();
        Path backupPath = Paths.get(backupDirectory);

        if (!Files.exists(backupPath)) {
            return backups;
        }

        try (Stream<Path> paths = Files.list(backupPath)) {
            paths.filter(p -> p.toString().endsWith(".sql"))
                    .forEach(p -> {
                        try {
                            File file = p.toFile();
                            backups.add(new BackupInfo(
                                    file.getName(),
                                    file.length(),
                                    Files.getLastModifiedTime(p).toMillis()));
                        } catch (IOException e) {
                            System.err.println("Error reading backup file: " + e.getMessage());
                        }
                    });
        }

        backups.sort(Comparator.comparing(BackupInfo::getTimestamp).reversed());
        return backups;
    }

    /**
     * Get backup file path for download
     */
    public Path getBackupFile(String filename) {
        return Paths.get(backupDirectory, filename);
    }

    /**
     * Clean backups older than retention period
     */
    private void cleanOldBackups() throws IOException {
        Path backupPath = Paths.get(backupDirectory);
        if (!Files.exists(backupPath)) {
            return;
        }

        long cutoffTime = System.currentTimeMillis() - (RETENTION_DAYS * 24L * 60 * 60 * 1000);

        try (Stream<Path> paths = Files.list(backupPath)) {
            paths.filter(p -> p.toString().endsWith(".sql"))
                    .filter(p -> {
                        try {
                            return Files.getLastModifiedTime(p).toMillis() < cutoffTime;
                        } catch (IOException e) {
                            return false;
                        }
                    })
                    .forEach(p -> {
                        try {
                            Files.delete(p);
                            System.out.println("Deleted old backup: " + p.getFileName());
                        } catch (IOException e) {
                            System.err.println("Failed to delete old backup: " + e.getMessage());
                        }
                    });
        }
    }

    private String extractDatabaseName(String jdbcUrl) {
        // Extract from jdbc:postgresql://localhost:5432/ktraders
        int lastSlash = jdbcUrl.lastIndexOf('/');
        int questionMark = jdbcUrl.indexOf('?', lastSlash);
        if (questionMark > 0) {
            return jdbcUrl.substring(lastSlash + 1, questionMark);
        }
        return jdbcUrl.substring(lastSlash + 1);
    }

    public static class BackupInfo {
        private String filename;
        private long size;
        private long timestamp;

        public BackupInfo(String filename, long size, long timestamp) {
            this.filename = filename;
            this.size = size;
            this.timestamp = timestamp;
        }

        public String getFilename() {
            return filename;
        }

        public long getSize() {
            return size;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public String getFormattedSize() {
            if (size < 1024)
                return size + " B";
            if (size < 1024 * 1024)
                return String.format("%.2f KB", size / 1024.0);
            return String.format("%.2f MB", size / (1024.0 * 1024));
        }
    }
}

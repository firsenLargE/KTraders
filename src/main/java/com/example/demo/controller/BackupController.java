package com.example.demo.controller;

import com.example.demo.service.BackupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.Path;

@Controller
@RequestMapping("/admin/backup")
public class BackupController {

    @Autowired
    private BackupService backupService;

    @GetMapping
    public String showBackupPage(Model model) {
        try {
            model.addAttribute("backups", backupService.getBackupHistory());
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load backup history: " + e.getMessage());
        }
        return "backup";
    }

    @PostMapping("/create")
    public String createBackup(RedirectAttributes redirectAttributes) {
        try {
            String filename = backupService.createBackup();
            redirectAttributes.addFlashAttribute("success", "Backup created successfully: " + filename);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Backup failed: " + e.getMessage());
        }
        return "redirect:/admin/backup";
    }

    @GetMapping("/download/{filename}")
    public ResponseEntity<Resource> downloadBackup(@PathVariable String filename) {
        try {
            Path filePath = backupService.getBackupFile(filename);
            Resource resource = new FileSystemResource(filePath);

            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}

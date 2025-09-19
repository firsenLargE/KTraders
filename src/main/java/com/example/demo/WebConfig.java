package com.example.demo;

import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve files from configurable directory
        String location = "file:" + uploadDir + (uploadDir.endsWith("/") ? "" : "/");
        registry.addResourceHandler("/uploads/**").addResourceLocations(location);
    }

    // Optional: ensure directory exists at boot
    @jakarta.annotation.PostConstruct
    public void ensureUploadDir() throws Exception {
        Path p = Path.of(uploadDir);
        if (!Files.exists(p)) Files.createDirectories(p);
    }
}

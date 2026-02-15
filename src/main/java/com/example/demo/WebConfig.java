package com.example.demo;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.lang.NonNull;

@Configuration
public class WebConfig implements WebMvcConfigurer {

        @org.springframework.beans.factory.annotation.Value("${app.upload.base-dir:uploads}")
        private String uploadBaseDir;

        @Override
        public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
                // Serve uploaded images from configured uploads directory
                String uploadPath = java.nio.file.Paths.get(uploadBaseDir).toAbsolutePath().toUri().toString();
                if (!uploadPath.endsWith("/")) {
                        uploadPath += "/";
                }
                System.out.println("DEBUG: uploadPath configured as: " + uploadPath);
                registry.addResourceHandler("/uploads/**")
                                .addResourceLocations(uploadPath);

                // Serve static resources
                registry.addResourceHandler("/static/**")
                                .addResourceLocations("classpath:/static/");

                registry.addResourceHandler("/css/**")
                                .addResourceLocations("classpath:/static/css/");

                registry.addResourceHandler("/js/**")
                                .addResourceLocations("classpath:/static/js/");

                registry.addResourceHandler("/images/**")
                                .addResourceLocations("classpath:/static/images/");
        }
}

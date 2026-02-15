package com.example.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class ImagePathPrinter implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    public ImagePathPrinter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("--- CHECKING IMAGE PATHS ---");
        try {
            List<Map<String, Object>> results = jdbcTemplate
                    .queryForList("SELECT id, image_path FROM products WHERE image_path IS NOT NULL");
            if (results.isEmpty()) {
                System.out.println("No products found with image_path set.");
            } else {
                for (Map<String, Object> row : results) {
                    System.out.println("Product ID: " + row.get("id") + ", Path: " + row.get("image_path"));
                }
            }
        } catch (Exception e) {
            System.out.println("Error querying database: " + e.getMessage());
        }
        System.out.println("--- END CHECK ---");
    }
}

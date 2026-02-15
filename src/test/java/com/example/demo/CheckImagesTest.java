package com.example.demo;

import org.junit.jupiter.api.Test;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class CheckImagesTest {

    private static final String PG_URL = "jdbc:postgresql://localhost:5432/kapiltradersdb";
    private static final String PG_USER = "zion";
    private static final String PG_PASSWORD = "zion";

    // Base directory for uploads as per application-local.properties
    private static final String BASE_DIR = "./data";

    public static void main(String[] args) {
        new CheckImagesTest().checkImagePaths();
    }

    @Test
    public void checkImagePaths() {
        System.out.println("Checking Image Paths...");

        try (Connection conn = DriverManager.getConnection(PG_URL, PG_USER, PG_PASSWORD);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT id, name, imagepath FROM products")) { // Note: column names
                                                                                                // are lowercase in
                                                                                                // Postgres
                                                                                                // imagePath
                                                                                                // might be case
                                                                                                // sensitive if
                                                                                                // migrated that
                                                                                                // way

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String dbPath = rs.getString("imagePath");

                if (dbPath != null && !dbPath.isEmpty()) {
                    // dbPath usually starts with /uploads/...
                    // We need to map it to ./data/uploads/...
                    String fsPath = BASE_DIR + dbPath;
                    File f = new File(fsPath);
                    if (f.exists()) {
                        System.out.println("[OK] ID " + id + ": " + fsPath);
                    } else {
                        // Try without leading slash if logic differs
                        File f2 = new File(BASE_DIR + (dbPath.startsWith("/") ? dbPath : "/" + dbPath));
                        if (f2.exists()) {
                            System.out.println("[OK] ID " + id + ": " + f2.getPath());
                        } else {
                            System.err.println("[MISSING] ID " + id + " (" + name + "): " + dbPath + " -> Checked: "
                                    + f.getAbsolutePath());
                        }
                    }
                } else {
                    System.out.println("[NO IMAGE] ID " + id + " (" + name + ")");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            // Try fallback query if case sensitivity is issue
            System.err.println("Query failed, retrying with lowercase column...");
            try (Connection conn = DriverManager.getConnection(PG_URL, PG_USER, PG_PASSWORD);
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT id, name, imagepath FROM products")) {

                while (rs.next()) {
                    System.out.println("Found product: " + rs.getString("name"));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}

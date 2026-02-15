package com.example.demo;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MigrationTool {

    // H2 Connection Details
    private static final String H2_URL = "jdbc:h2:file:./data/kapiltraders"; // Assuming standard MV
    private static final String H2_USER = "sa"; // Default H2 user
    private static final String H2_PASSWORD = "";

    // PostgreSQL Connection Details
    private static final String PG_URL = "jdbc:postgresql://localhost:5432/kapiltradersdb";
    private static final String PG_USER = "zion";
    private static final String PG_PASSWORD = "zion";

    public static void main(String[] args) {
        System.out.println("Starting Migration...");

        try (Connection h2Conn = DriverManager.getConnection(H2_URL, H2_USER, H2_PASSWORD);
                Connection pgConn = DriverManager.getConnection(PG_URL, PG_USER, PG_PASSWORD)) {

            System.out.println("Connected to both H2 and PostgreSQL.");

            // 1. Get List of Tables from H2
            List<String> tables = getTables(h2Conn);
            System.out.println("Found tables in H2: " + tables);

            // 2. Disable Foreign Keys in Postgres (Session only)
            try (Statement stmt = pgConn.createStatement()) {
                stmt.execute("SET session_replication_role = 'replica';");
            }

            // 3. Migrate Data for each table
            for (String table : tables) {
                migrateTable(h2Conn, pgConn, table);
            }

            // 4. Reset Sequences (for serial columns if needed)
            // This is complex to do generically, but we can try for standard 'id' columns
            resetSequences(pgConn, tables);

            // 5. Re-enable Foreign Keys
            try (Statement stmt = pgConn.createStatement()) {
                stmt.execute("SET session_replication_role = 'origin';");
            }

            System.out.println("Migration Completed Successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Migration Failed: " + e.getMessage());
        }
    }

    private static List<String> getTables(Connection conn) throws SQLException {
        List<String> tables = new ArrayList<>();
        try (ResultSet rs = conn.getMetaData().getTables(null, "PUBLIC", "%", new String[] { "TABLE" })) {
            while (rs.next()) {
                tables.add(rs.getString("TABLE_NAME"));
            }
        }
        return tables;
    }

    private static void migrateTable(Connection h2Conn, Connection pgConn, String tableName) throws SQLException {
        System.out.println("Migrating table: " + tableName);

        // Check if table exists in PG - if not, we skip (assuming schema is created by
        // Hibernate)
        if (!tableExists(pgConn, tableName)) {
            System.out.println("Table " + tableName + " does not exist in PostgreSQL. Skipping.");
            return;
        }

        // Clean existing data in PG (Optional - be careful!)
        try (Statement stmt = pgConn.createStatement()) {
            stmt.execute("TRUNCATE TABLE \"" + tableName + "\" CASCADE");
        } catch (SQLException e) {
            System.out.println(
                    "Metrics: Could not truncate " + tableName + " (might be empty or new). " + e.getMessage());
        }

        try (Statement h2Stmt = h2Conn.createStatement();
                ResultSet rs = h2Stmt.executeQuery("SELECT * FROM \"" + tableName + "\"")) {

            int colCount = rs.getMetaData().getColumnCount();
            StringBuilder insertSQL = new StringBuilder("INSERT INTO \"" + tableName + "\" VALUES (");
            for (int i = 0; i < colCount; i++) {
                insertSQL.append(i == 0 ? "?" : ", ?");
            }
            insertSQL.append(")");

            try (java.sql.PreparedStatement pgStmt = pgConn.prepareStatement(insertSQL.toString())) {
                int count = 0;
                while (rs.next()) {
                    for (int i = 1; i <= colCount; i++) {
                        pgStmt.setObject(i, rs.getObject(i));
                    }
                    pgStmt.addBatch();
                    count++;
                    if (count % 100 == 0)
                        pgStmt.executeBatch();
                }
                pgStmt.executeBatch();
                System.out.println("Migrated " + count + " rows for " + tableName);
            }
        }
    }

    private static boolean tableExists(Connection conn, String tableName) throws SQLException {
        try (ResultSet rs = conn.getMetaData().getTables(null, null, tableName, null)) { // PostgreSQL schema often
                                                                                         // public
            return rs.next();
        }
    }

    private static void resetSequences(Connection conn, List<String> tables) {
        // Checking for 'id' column and 'tablename_id_seq'
        for (String table : tables) {
            try (Statement stmt = conn.createStatement()) {
                // Try standard convention: table_id_seq
                String seqName = table + "_id_seq"; // standard hibernate/postgres
                // Check max id
                ResultSet rs = stmt.executeQuery("SELECT MAX(id) FROM \"" + table + "\"");
                if (rs.next()) {
                    long maxId = rs.getLong(1);
                    stmt.execute("SELECT setval('" + seqName + "', " + (maxId + 1) + ", false)");
                    System.out.println("Reset sequence " + seqName + " to " + (maxId + 1));
                }
            } catch (Exception e) {
                // Ignore, sequence might not exist or column not named ID
            }
        }
    }
}

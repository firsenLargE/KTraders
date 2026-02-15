package com.example.demo;

import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MigrationTest {

    // H2 Connection Details
    private static final String H2_URL = "jdbc:h2:file:./data/kapiltraders";
    private static final String H2_USER = "sa";
    private static final String H2_PASSWORD = "";

    // PostgreSQL Connection Details
    private static final String PG_URL = "jdbc:postgresql://localhost:5432/kapiltradersdb";
    private static final String PG_USER = "zion";
    private static final String PG_PASSWORD = "zion";

    @Test
    public void runMigration() {
        System.out.println("Starting Migration Test...");

        try (Connection h2Conn = DriverManager.getConnection(H2_URL, H2_USER, H2_PASSWORD);
                Connection pgConn = DriverManager.getConnection(PG_URL, PG_USER, PG_PASSWORD)) {

            System.out.println("Connected to both H2 and PostgreSQL.");

            List<String> tables = getTables(h2Conn);
            System.out.println("Found tables in H2: " + tables);

            // Disable Foreign Keys temporarily (using triggers)
            for (String table : tables) {
                try (Statement stmt = pgConn.createStatement()) {
                    stmt.execute("ALTER TABLE \"" + table.toLowerCase() + "\" DISABLE TRIGGER ALL");
                } catch (SQLException e) {
                    System.out.println("Could not disable triggers for " + table + ": " + e.getMessage());
                }
            }

            for (String table : tables) {
                migrateTable(h2Conn, pgConn, table);
            }

            resetSequences(pgConn, tables);

            // Re-enable Foreign Keys
            for (String table : tables) {
                try (Statement stmt = pgConn.createStatement()) {
                    stmt.execute("ALTER TABLE \"" + table.toLowerCase() + "\" ENABLE TRIGGER ALL");
                } catch (SQLException e) {
                    System.out.println("Could not enable triggers for " + table + ": " + e.getMessage());
                }
            }

            System.out.println("Migration Completed Successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Migration Failed: " + e.getMessage());
            throw new RuntimeException("Migration failed", e);
        }
    }

    private List<String> getTables(Connection conn) throws SQLException {
        List<String> tables = new ArrayList<>();
        try (ResultSet rs = conn.getMetaData().getTables(null, "PUBLIC", "%", new String[] { "TABLE" })) {
            while (rs.next()) {
                tables.add(rs.getString("TABLE_NAME"));
            }
        }
        return tables;
    }

    private void migrateTable(Connection h2Conn, Connection pgConn, String tableName) throws SQLException {
        String pgTableName = tableName.toLowerCase(); // Map H2 UPPERCASE to Postgres lowercase
        System.out.println("Migrating table: " + tableName + " -> " + pgTableName);

        if (!tableExists(pgConn, pgTableName)) {
            System.out.println("Table " + pgTableName + " does not exist in PostgreSQL. Skipping.");
            return;
        }

        try (Statement stmt = pgConn.createStatement()) {
            stmt.execute("TRUNCATE TABLE \"" + pgTableName + "\" CASCADE");
        } catch (SQLException e) {
            System.out.println("Could not truncate " + pgTableName + ". " + e.getMessage());
        }

        try (Statement h2Stmt = h2Conn.createStatement();
                ResultSet rs = h2Stmt.executeQuery("SELECT * FROM \"" + tableName + "\"")) {

            int colCount = rs.getMetaData().getColumnCount();
            StringBuilder insertSQL = new StringBuilder("INSERT INTO \"" + pgTableName + "\" VALUES (");
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

    private boolean tableExists(Connection conn, String tableName) throws SQLException {
        try (ResultSet rs = conn.getMetaData().getTables(null, null, tableName, null)) {
            return rs.next();
        }
    }

    private void resetSequences(Connection conn, List<String> tables) {
        for (String table : tables) {
            String pgTable = table.toLowerCase();
            try (Statement stmt = conn.createStatement()) {
                String seqName = pgTable + "_id_seq";
                ResultSet rs = stmt.executeQuery("SELECT MAX(id) FROM \"" + pgTable + "\"");
                if (rs.next()) {
                    long maxId = rs.getLong(1);
                    stmt.execute("SELECT setval('" + seqName + "', " + (maxId + 1) + ", false)");
                    System.out.println("Reset sequence " + seqName + " to " + (maxId + 1));
                }
            } catch (Exception e) {
                // Ignore
            }
        }
    }
}

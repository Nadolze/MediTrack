package com.meditrack.shared.infrastructure;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Einfache Gesundheitsprüfung der Datenbank beim Start der Anwendung.
 *
 * - Führt ein "SELECT 1" aus.
 * - Erkennt anhand der JDBC-URL, ob die Server-DB (MySQL) oder H2 verwendet wird.
 *
 * Diese Klasse wird mit @Order(1) VOR DatabaseCreateTables ausgeführt.
 */
@Component
@Order(1)
public class DatabaseHealthCheck implements CommandLineRunner {

    private final DataSource dataSource;

    /**
     * Konstruktor-Injektion des DataSource-Beans.
     *
     * @param dataSource von Spring konfiguriertes DataSource-Objekt
     */
    public DatabaseHealthCheck(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("🔍 Starte Datenbank-Verbindungstest (SELECT 1)...");

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT 1");
             ResultSet rs = ps.executeQuery()) {

            DatabaseMetaData metaData = connection.getMetaData();
            String jdbcUrl = metaData.getURL();
            String dbProduct = metaData.getDatabaseProductName();

            boolean isMySql = jdbcUrl != null && jdbcUrl.startsWith("jdbc:mysql");
            boolean isH2 = jdbcUrl != null && jdbcUrl.startsWith("jdbc:h2");

            if (rs.next()) {
                int value = rs.getInt(1);

                if (isMySql) {
                    System.out.println("✅ Server-Datenbank (MySQL) erreichbar. Ergebnis von SELECT 1 = " + value);
                    System.out.println("   → JDBC-URL: " + jdbcUrl);
                } else if (isH2) {
                    System.out.println("✅ H2-Datenbank aktiv (lokaler Dev/Fallback). Ergebnis von SELECT 1 = " + value);
                    System.out.println("   → JDBC-URL: " + jdbcUrl);
                } else {
                    System.out.println("✅ Datenbank-Verbindung erfolgreich getestet. Ergebnis von SELECT 1 = " + value);
                    System.out.println("   → DB-Typ: " + dbProduct + ", JDBC-URL: " + jdbcUrl);
                }

            } else {
                System.out.println("⚠️ DB-Verbindung hergestellt, aber SELECT 1 lieferte kein Ergebnis.");
                System.out.println("   → DB-Typ: " + dbProduct + ", JDBC-URL: " + jdbcUrl);
            }

        } catch (Exception ex) {
            System.err.println("❌ Fehler beim Testen der DB-Verbindung: " + ex.getMessage());
            System.err.println("❌ Konnte keine Verbindung zur konfigurierten Datenbank aufbauen.");
            System.err.println("   → Prüfe ggf. deine Database.env bzw. die Server-Datenbank-Konfiguration.");
            // Fehler weiterwerfen, damit der Start klar fehlschlägt
            throw ex;
        }
    }
}

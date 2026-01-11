package com.meditrack.shared.infrastructure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * Infrastruktur-Klasse zur Initialisierung der Datenbank.
 */
public class DatabaseCreateTables {

    private static final Logger log = LoggerFactory.getLogger(DatabaseCreateTables.class);

    // Zentrale Infrastruktur-Abhängigkeiten
    private final DataSource dataSource;
    private final Environment env;
    private final PasswordEncoder passwordEncoder;

    public DatabaseCreateTables(DataSource dataSource, Environment env, PasswordEncoder passwordEncoder) {
        this.dataSource = dataSource;
        this.env = env;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Einstiegspunkt für DB-Initialisierung.
     * Steuert Schema, Reset und Seed anhand von Properties.
     */
    public void run() {
        // Globale Aktivierung / Deaktivierung
        boolean initEnabled = bool("meditrack.db.init.enabled", true);
        if (!initEnabled) {
            log.info("🧱 DB-Init ist deaktiviert (meditrack.db.init.enabled=false).");
            return;
        }

        // DB-Typ erkennen
        String url = jdbcUrl();
        boolean isH2 = url.toLowerCase(Locale.ROOT).startsWith("jdbc:h2:");
        boolean isMySql = url.toLowerCase(Locale.ROOT).startsWith("jdbc:mysql:");

        // Steuerflags
        boolean dropAndRecreate = bool("meditrack.db.init.drop-and-recreate", false);
        boolean reset = bool("meditrack.db.init.reset", false);

        // Safety: Remote-Reset/Drop nur wenn explizit erlaubt
        if ((dropAndRecreate || reset) && isMySql && !bool("meditrack.db.init.allow-remote-reset", false)) {
            throw new IllegalStateException(
                    "Remote MySQL Reset/Drop ist gesperrt. Setze meditrack.db.init.allow-remote-reset=true, wenn du das wirklich willst."
            );
        }

        try {
            if (dropAndRecreate) {
                // komplettes Neuaufsetzen
                log.warn("🧨 DROP+RECREATE aktiv – Tabellen werden neu erstellt.");
                executeScript(isH2 ? "db/drop-h2.sql" : "db/drop-mysql.sql");
            }

            // Schema-Erstellung
            log.info("🧱 Schema init: {}", isH2 ? "H2" : (isMySql ? "MySQL" : "UNKNOWN"));
            executeScript(isH2 ? "db/schema-h2.sql" : "db/schema-mysql.sql");

            // Tabellen leeren
            if (reset) {
                log.warn("🧨 DEV-RESET aktiv – Tabellen werden geleert.");
                resetAllTables(isH2);
            }

            // Beispiel-Daten anlegen
            boolean seedEnabled = bool("meditrack.db.seed.enabled", false) || env.getProperty("MEDITRACK_SEED_PASSWORD") != null;
            if (seedEnabled) {
                seedUsersAndStaff();
            } else {
                log.info("🌱 SEED ist deaktiviert (meditrack.db.seed.enabled=false und kein MEDITRACK_SEED_PASSWORD).");
            }

        } catch (Exception e) {
            log.error("❌ Schema/Seed fehlgeschlagen", e);
            throw (e instanceof RuntimeException re) ? re : new IllegalStateException(e);
        }
    }

    /**
     * Führt ein SQL-Skript aus dem Classpath aus.
     */
    private void executeScript(String classpathSql) {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource(classpathSql));
        populator.setContinueOnError(false);
        populator.execute(dataSource);
    }

    /**
     * Leert alle existierenden mt_* Tabellen
     * unter Beachtung von FK-Abhängigkeiten.
     */
    private void resetAllTables(boolean isH2) {
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);

        // Wichtig: Im aktuellen Stand existieren (noch) nicht alle Tabellen in jeder Umgebung.
        // Daher: nur *existierende* mt_* Tabellen leeren, in stabiler Reihenfolge.
        // (Das behebt auch den Testfehler: TRUNCATE mt_notification obwohl die Tabelle noch nicht angelegt ist.)
        List<String> tables = resolveTablesForReset();
        if (tables.isEmpty()) {
            log.warn("🧹 Reset: Keine mt_* Tabellen gefunden – nichts zu leeren.");
            return;
        }

        if (isH2) {
            jdbc.execute("SET REFERENTIAL_INTEGRITY FALSE");
        } else {
            jdbc.execute("SET FOREIGN_KEY_CHECKS=0");
        }

        for (String t : tables) {
            jdbc.execute("TRUNCATE TABLE " + quoteIdentifier(t, isH2));
        }

        if (isH2) {
            jdbc.execute("SET REFERENTIAL_INTEGRITY TRUE");
        } else {
            jdbc.execute("SET FOREIGN_KEY_CHECKS=1");
        }
    }

    /**
     * Ermittelt alle existierenden mt_* Tabellen
     * in stabiler (Child → Parent) Reihenfolge.
     */
    private List<String> resolveTablesForReset() {
        try (Connection con = dataSource.getConnection()) {
            DatabaseMetaData meta = con.getMetaData();
            String catalog = con.getCatalog();
            String schema = con.getSchema();

            // 1) Alle mt_* Tabellen einsammeln
            // Pattern nutzt JDBC-LIKE: "MT_%" passt zu "MT_...".
            var lowerToActual = new java.util.LinkedHashMap<String, String>();
            try (ResultSet rs = meta.getTables(catalog, schema, "MT_%", new String[]{"TABLE"})) {
                while (rs.next()) {
                    String table = rs.getString("TABLE_NAME");
                    if (table != null) {
                        lowerToActual.put(table.toLowerCase(Locale.ROOT), table);
                    }
                }
            }

            if (lowerToActual.isEmpty()) {
                return List.of();
            }

            // 2) Stabile Reihenfolge (Child -> Parent). Nur übernehmen, wenn existierend.
            List<String> preferred = List.of(
                    "mt_notification",
                    "mt_alert",
                    "mt_vital_reading",
                    "mt_vital_threshold",
                    "mt_medication_plan_item",
                    "mt_medication_plan",
                    "mt_medication",
                    "mt_medical_staff",
                    "mt_patient",
                    "mt_user"
            );

            List<String> ordered = new ArrayList<>();
            for (String p : preferred) {
                String actual = lowerToActual.remove(p);
                if (actual != null) {
                    ordered.add(actual);
                }
            }

            // 3) Restliche mt_* Tabellen (falls später neue dazukommen)
            ordered.addAll(lowerToActual.values());
            return ordered;
        } catch (SQLException e) {
            throw new IllegalStateException("Konnte Tabellenliste für Reset nicht ermitteln", e);
        }
    }

    private String quoteIdentifier(String identifier, boolean isH2) {
        // Für MySQL sind Backticks robust. In H2 (auch MODE=MySQL) sind unquoted/upper-case ok.
        // Da wir bei H2 ohnehin ohne Quotes arbeiten, vermeiden wir hier Dialekt-Kanten.
        if (isH2) return identifier;
        return "`" + identifier + "`";
    }

    /**
     * Legt Beispiel-User und Medical-Staff an.
     * Passwort wird aus Environment gelesen.
     */
    private void seedUsersAndStaff() {
        String seedPw = env.getProperty("MEDITRACK_SEED_PASSWORD");
        if (seedPw == null || seedPw.isBlank()) {
            throw new IllegalStateException("SEED aktiviert, aber MEDITRACK_SEED_PASSWORD fehlt/leer.");
        }

        JdbcTemplate jdbc = new JdbcTemplate(dataSource);

        // Beispiel-Accounts (wie in deinem Log)
        List<SeedUser> users = List.of(
                new SeedUser("wolfdeleuAD", "wolfdeleuAD@meditrack.com", "ADMIN", true),
                new SeedUser("konstantinAD", "konstantinAD@meditrack.com", "ADMIN", true),
                new SeedUser("timoAD", "timoAD@meditrack.com", "ADMIN", true),
                new SeedUser("ferdiAD", "ferdiAD@meditrack.com", "ADMIN", true),
                new SeedUser("wolfdeleuST", "wolfdeleuST@meditrack.com", "STAFF", true),
                new SeedUser("konstantinST", "konstantinST@meditrack.com", "STAFF", true),
                new SeedUser("timoST", "timoST@meditrack.com", "STAFF", true),
                new SeedUser("ferdiST", "ferdiST@meditrack.com", "STAFF", true)
        );

        String pwHash = passwordEncoder.encode(seedPw);

        for (SeedUser u : users) {
            String userId = upsertUser(jdbc, u, pwHash);

            if (u.createStaff) {
                upsertMedicalStaff(jdbc, userId, u.username);
            }
        }

        log.warn("🌱 SEED abgeschlossen.");
    }

    /**
     * Insert oder Update eines Users anhand der E-Mail.
     */
    private String upsertUser(JdbcTemplate jdbc, SeedUser u, String pwHash) {
        // Existiert per email?
        List<String> ids = jdbc.query(
                "SELECT id FROM mt_user WHERE email = ?",
                (rs, rowNum) -> rs.getString("id"),
                u.email
        );

        if (ids.isEmpty()) {
            String id = UUID.randomUUID().toString();
            jdbc.update(
                    "INSERT INTO mt_user (id, name, email, password_hash, role) VALUES (?,?,?,?,?)",
                    id, u.username, u.email, pwHash, u.role
            );
            log.info("➕ inserted user: {} ({}) role={}", u.username, u.email, u.role);
            return id;
        } else {
            String id = ids.get(0);
            jdbc.update(
                    "UPDATE mt_user SET name=?, password_hash=?, role=? WHERE id=?",
                    u.username, pwHash, u.role, id
            );
            log.info("♻️ updated user: {} ({}) role={}", u.username, u.email, u.role);
            return id;
        }
    }

    /**
     * Insert oder Update eines Medical-Staff-Datensatzes.
     * Berücksichtigt optionale Spalten.
     */
    private void upsertMedicalStaff(JdbcTemplate jdbc, String userId, String username) {
        boolean hasFirstName = columnExists("mt_medical_staff", "first_name");
        boolean hasLastName = columnExists("mt_medical_staff", "last_name");

        String firstName = username;
        String lastName = username;

        // id ist eigener staff-id (nicht user-id)
        String staffId = UUID.randomUUID().toString();

        if (hasFirstName && hasLastName) {
            // mt_medical_staff(id, user_id, display_name, first_name, last_name)
            jdbc.update("""
                INSERT INTO mt_medical_staff (id, user_id, display_name, first_name, last_name)
                VALUES (?,?,?,?,?)
                ON DUPLICATE KEY UPDATE
                  display_name=VALUES(display_name),
                  first_name=VALUES(first_name),
                  last_name=VALUES(last_name)
                """, staffId, userId, username, firstName, lastName
            );
        } else {
            jdbc.update("""
                INSERT INTO mt_medical_staff (id, user_id, display_name)
                VALUES (?,?,?)
                ON DUPLICATE KEY UPDATE
                  display_name=VALUES(display_name)
                """, staffId, userId, username
            );
        }

        log.info("   ➕/♻️ upserted mt_medical_staff for {} (user_id={})", username, userId);
    }

    /**
     * Prüft, ob eine Spalte in einer Tabelle existiert.
     */
    private boolean columnExists(String table, String column) {
        try (Connection c = dataSource.getConnection()) {
            DatabaseMetaData md = c.getMetaData();
            try (ResultSet rs = md.getColumns(null, null, table, column)) {
                if (rs.next()) return true;
            }
            // Manche DBs liefern Uppercase zurück
            try (ResultSet rs2 = md.getColumns(null, null, table.toUpperCase(Locale.ROOT), column.toUpperCase(Locale.ROOT))) {
                return rs2.next();
            }
        } catch (Exception e) {
            log.warn("Konnte Spalten-Existenz nicht prüfen ({}.{}) – nehme false an. {}", table, column, e.getMessage());
        }
        return false;
    }

    /**
     * Liest Boolean-Property mit Default.
     */
    private boolean bool(String key, boolean def) {
        String v = env.getProperty(key);
        return (v == null) ? def : Boolean.parseBoolean(v);
    }

    /**
     * Ermittelt JDBC-URL der aktuellen Verbindung.
     */
    private String jdbcUrl() {
        try (Connection c = dataSource.getConnection()) {
            return c.getMetaData().getURL();
        } catch (Exception e) {
            return "unknown";
        }
    }

    /**
     * Interne Struktur für Seed-Definitionen.
     */
    private record SeedUser(String username, String email, String role, boolean createStaff) {}
}

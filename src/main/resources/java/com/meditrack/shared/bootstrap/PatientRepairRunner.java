package com.meditrack.shared.bootstrap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Repair/Bootstrap für die Datenbank:
 *
 * Hintergrund:
 * - mt_medication_plan.patient_id hat FK auf mt_patient.id
 * - In der App nutzen wir patientId = mt_user.id (Session userId)
 * - Der SQL-Dump löscht mt_patient häufig komplett.
 *
 * Lösung:
 * - Beim Start synchronisieren wir alle mt_user(role='PATIENT') nach mt_patient,
 *   falls dort der Datensatz noch nicht existiert.
 *
 * Wir setzen:
 * - mt_patient.id     = mt_user.id
 * - mt_patient.user_id= mt_user.id
 * - first_name        = mt_user.name (fallback: id)
 * - last_name         = "" (NOT NULL)
 *
 * Diese Repair ist idempotent (kann beliebig oft laufen).
 */
@Component
public class PatientRepairRunner implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public PatientRepairRunner(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (jdbcTemplate == null) {
            return;
        }

        try {
            // Alle PATIENT-User laden
            List<UserRow> patientUsers = jdbcTemplate.query(
                    "SELECT id, name FROM mt_user WHERE role = 'PATIENT'",
                    (rs, rowNum) -> new UserRow(rs.getString("id"), rs.getString("name"))
            );

            for (UserRow u : patientUsers) {
                ensurePatientRowExists(u.id(), u.name());
            }
        } catch (Exception ignored) {
            // bewusst still: App soll auch ohne DB (oder mit abweichendem Schema) hochfahren
        }
    }

    private void ensurePatientRowExists(String userId, String username) {
        if (userId == null || userId.isBlank()) {
            return;
        }

        try {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM mt_patient WHERE id = ?",
                    Integer.class,
                    userId
            );

            if (count != null && count > 0) {
                return;
            }

            String firstName = (username == null || username.isBlank()) ? userId : username;

            jdbcTemplate.update(
                    "INSERT INTO mt_patient (id, user_id, first_name, last_name) VALUES (?, ?, ?, ?)",
                    userId,
                    userId,
                    firstName,
                    ""
            );
        } catch (Exception ignored) {
            // bewusst still
        }
    }

    private record UserRow(String id, String name) {}
}

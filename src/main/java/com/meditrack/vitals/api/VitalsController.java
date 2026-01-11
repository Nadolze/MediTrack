package com.meditrack.vitals.api;

import com.meditrack.shared.api.SessionKeys;
import com.meditrack.shared.valueobject.UserSession;
import com.meditrack.vitals.domain.entity.VitalReading;
import com.meditrack.vitals.domain.events.VitalReadingCreatedEvent;
import com.meditrack.vitals.domain.repository.VitalReadingRepository;
import com.meditrack.vitals.domain.valueobject.MeasurementValue;
import com.meditrack.vitals.domain.valueobject.PatientId;
import com.meditrack.vitals.domain.valueobject.Unit;
import com.meditrack.vitals.domain.valueobject.VitalType;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Web-Controller für den BC "vitals".
 *
 * - STAFF/ADMIN: Patient-Kontext wird in der Session gespeichert (ACTIVE_PATIENT_ID),
 *   damit Redirects stabil bleiben (keine "leere Seite").
 * - Alerts-BC: Nach dem Speichern wird ein VitalReadingCreatedEvent published,
 *   damit der Alerts-Listener neue Alerts erzeugen kann.
 */
@Controller
@RequestMapping("/vitals/readings")
public class VitalsController {

    // Repository für Persistenz der Vitalwerte
    private final VitalReadingRepository vitalReadingRepository;

    // EventPublisher für BC-übergreifende Reaktionen (z.B. Alerts)
    private final ApplicationEventPublisher eventPublisher;

    // Optionaler JdbcTemplate für einfache DB-Zugriffe.
    @Autowired(required = false)
    private JdbcTemplate jdbcTemplate;

    public VitalsController(VitalReadingRepository vitalReadingRepository,
                            ApplicationEventPublisher eventPublisher) {
        this.vitalReadingRepository = vitalReadingRepository;
        this.eventPublisher = eventPublisher;
    }

    /** Dropdown-Option für STAFF/ADMIN. */
    public record PatientOption(String id, String label) {}

    /**
     * Liste aller Vitalwerte für den effektiven Patienten.
     * STAFF/ADMIN können Patient wählen, PATIENT sieht nur sich selbst.
     */
    @GetMapping
    public String list(
            @RequestParam(value = "patientId", required = false) String patientIdParam,
            Model model,
            HttpSession session
    ) {
        // Auth-Check
        UserSession user = (UserSession) session.getAttribute(SessionKeys.LOGGED_IN_USER);
        if (user == null) {
            return "redirect:/login";
        }

        boolean canSelectPatient = user.hasAnyRole("ADMIN", "STAFF");

        String effectivePatientId;
        if (canSelectPatient) {
            // Patient aus Request oder aus Session (ACTIVE_PATIENT_ID)
            String fromParam = (patientIdParam == null) ? "" : patientIdParam.trim();

            if (fromParam.isBlank()) {
                Object active = session.getAttribute(SessionKeys.ACTIVE_PATIENT_ID);
                fromParam = (active instanceof String) ? ((String) active).trim() : "";
            } else {
                // explizite Auswahl merken
                session.setAttribute(SessionKeys.ACTIVE_PATIENT_ID, fromParam);
            }

            effectivePatientId = fromParam;
            // Dropdown-Liste für Auswahl laden
            model.addAttribute("patients", loadPatientsIfPossible());
        } else {
            // Patient sieht nur seine eigenen Daten
            effectivePatientId = user.getUserId();
        }

        // Gemeinsame View-Attribute
        model.addAttribute("user", user);
        model.addAttribute("canSelectPatient", canSelectPatient);
        model.addAttribute("patientId", effectivePatientId);
        model.addAttribute("patientLabel", resolvePatientLabel(effectivePatientId));

        model.addAttribute(
                "readings",
                (effectivePatientId == null || effectivePatientId.isBlank())
                        ? List.of()
                        : vitalReadingRepository.findByPatientIdOrderByMeasuredAtDesc(effectivePatientId)
        );

        return "vitals/reading-list";
    }

    /**
     * Formular zum Anlegen eines neuen Vitalwerts.
     */
    @GetMapping("/new")
    public String form(
            @RequestParam(value = "patientId", required = false) String patientIdParam,
            Model model,
            HttpSession session
    ) {
        UserSession user = (UserSession) session.getAttribute(SessionKeys.LOGGED_IN_USER);
        if (user == null) {
            return "redirect:/login";
        }

        boolean canSelectPatient = user.hasAnyRole("ADMIN", "STAFF");

        String effectivePatientId;
        if (canSelectPatient) {
            // gleiche Patienten-Auflösung wie in der Liste
            String fromParam = (patientIdParam == null) ? "" : patientIdParam.trim();

            if (fromParam.isBlank()) {
                Object active = session.getAttribute(SessionKeys.ACTIVE_PATIENT_ID);
                fromParam = (active instanceof String) ? ((String) active).trim() : "";
            } else {
                session.setAttribute(SessionKeys.ACTIVE_PATIENT_ID, fromParam);
            }

            effectivePatientId = fromParam;
            model.addAttribute("patients", loadPatientsIfPossible());
        } else {
            effectivePatientId = user.getUserId();
        }

        model.addAttribute("user", user);
        model.addAttribute("canSelectPatient", canSelectPatient);
        model.addAttribute("patientId", effectivePatientId);
        model.addAttribute("patientLabel", resolvePatientLabel(effectivePatientId));

        // Enums für Select-Felder
        model.addAttribute("types", VitalType.values());
        model.addAttribute("units", Unit.values());

        return "vitals/reading-form";
    }

    /**
     * Speichert einen neuen Vitalwert.
     * STAFF/ADMIN dürfen für andere Patienten erfassen.
     */
    @PostMapping
    public String create(
            @RequestParam("vitalType") String vitalType,
            @RequestParam("valueNumeric") double valueNumeric,
            @RequestParam("unit") String unit,
            @RequestParam(value = "patientId", required = false) String patientIdFromForm,
            HttpSession session,
            RedirectAttributes ra
    ) {
        UserSession user = (UserSession) session.getAttribute(SessionKeys.LOGGED_IN_USER);
        if (user == null) {
            return "redirect:/login";
        }

        boolean staffOrAdmin = user.hasAnyRole("ADMIN", "STAFF");

        // Patient bestimmen (Form → Session → eigener User)
        String patientId;
        if (staffOrAdmin) {
            patientId = (patientIdFromForm == null) ? "" : patientIdFromForm.trim();

            if (patientId.isBlank()) {
                Object active = session.getAttribute(SessionKeys.ACTIVE_PATIENT_ID);
                patientId = (active instanceof String) ? ((String) active).trim() : "";
            }
        } else {
            patientId = user.getUserId();
        }

        if (patientId == null || patientId.isBlank()) {
            ra.addFlashAttribute("error", "Bitte Patient auswählen.");
            return "redirect:/vitals/readings/new";
        }

        // Sicherstellen, dass Patient in mt_patient existiert
        if (staffOrAdmin) {
            session.setAttribute(SessionKeys.ACTIVE_PATIENT_ID, patientId);
            ensurePatientRowExists(patientId);
        }

        // FK: recorded_by_staff_id muss mt_medical_staff.id sein
        String recordedByStaffId = null;
        if (staffOrAdmin) {
            recordedByStaffId = ensureMedicalStaffRowExistsForUser(user.getUserId(), user.getUsername());
        }

        // Domain-Objekt erzeugen
        VitalReading reading = VitalReading.create(
                new PatientId(patientId),
                VitalType.valueOf(vitalType),
                new MeasurementValue(valueNumeric),
                Unit.valueOf(unit),
                LocalDateTime.now(),
                recordedByStaffId
        );

        vitalReadingRepository.save(reading);

        // ✅ WICHTIG: Event für alerts-BC wieder publishen
        eventPublisher.publishEvent(new VitalReadingCreatedEvent(
                reading.getPatientId().value(),
                reading.getVitalReadingId().value(),
                reading.getType(),
                reading.getValue(),
                reading.getUnit()
        ));

        ra.addFlashAttribute("success", "Vitalwert gespeichert.");

        return "redirect:/vitals/readings";
    }

    /**
     * Lädt Patienten für Dropdown (nur wenn JdbcTemplate verfügbar).
     */
    private List<PatientOption> loadPatientsIfPossible() {
        if (jdbcTemplate == null) return Collections.emptyList();

        try {
            return jdbcTemplate.query(
                    "SELECT id, name, email FROM mt_user WHERE role = 'PATIENT' ORDER BY name",
                    (rs, rowNum) -> {
                        String id = rs.getString("id");
                        String name = rs.getString("name");
                        String email = rs.getString("email");
                        // Fallback-Reihenfolge für Anzeige
                        String label = (name != null && !name.isBlank())
                                ? name
                                : (email != null && !email.isBlank() ? email : id);
                        return new PatientOption(id, label);
                    }
            );
        } catch (Exception ignored) {
            return Collections.emptyList();
        }
    }

    /**
     * Stellt sicher, dass ein Patient-Datensatz existiert (FK-Sicherheit).
     */
    private void ensurePatientRowExists(String userId) {
        if (jdbcTemplate == null || userId == null || userId.isBlank()) return;

        try {
            Integer cnt = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM mt_patient WHERE id = ?",
                    Integer.class,
                    userId
            );
            if (cnt != null && cnt > 0) return;

            // Name aus mt_user ableiten
            String name;
            try {
                name = jdbcTemplate.queryForObject(
                        "SELECT name FROM mt_user WHERE id = ?",
                        String.class,
                        userId
                );
            } catch (Exception ignored) {
                name = userId;
            }

            if (name == null || name.isBlank()) name = userId;

            // primitive Vor-/Nachnamen-Aufteilung
            String first = name;
            String last = "";
            String[] parts = name.trim().split("\\s+", 2);
            if (parts.length == 2) {
                first = parts[0];
                last = parts[1];
            }

            jdbcTemplate.update(
                    "INSERT INTO mt_patient (id, user_id, first_name, last_name) VALUES (?,?,?,?)",
                    userId, userId, first, last
            );

        } catch (Exception ignored) {
        }
    }

    /**
     * Liefert oder erzeugt einen mt_medical_staff-Eintrag für den User.
     */
    private String ensureMedicalStaffRowExistsForUser(String userId, String username) {
        if (jdbcTemplate == null || userId == null || userId.isBlank()) return null;

        try {
            List<String> existing = jdbcTemplate.query(
                    "SELECT id FROM mt_medical_staff WHERE user_id = ?",
                    (rs, rowNum) -> rs.getString("id"),
                    userId
            );
            if (!existing.isEmpty() && existing.get(0) != null && !existing.get(0).isBlank()) {
                return existing.get(0);
            }
        } catch (Exception ignored) {
        }

        String staffId = UUID.randomUUID().toString();

        // Anzeige-Name bestimmen
        String displayName = (username == null || username.isBlank()) ? userId : username;
        try {
            String name = jdbcTemplate.queryForObject("SELECT name FROM mt_user WHERE id = ?", String.class, userId);
            if (name != null && !name.isBlank()) displayName = name;
        } catch (Exception ignored) {
        }

        String first = displayName;
        String last = "";
        String[] parts = displayName.trim().split("\\s+", 2);
        if (parts.length == 2) {
            first = parts[0];
            last = parts[1];
        }

        try {
            jdbcTemplate.update(
                    "INSERT INTO mt_medical_staff (id, user_id, display_name, first_name, last_name) VALUES (?,?,?,?,?)",
                    staffId, userId, displayName, first, last
            );
            return staffId;
        } catch (Exception ignored) {
            // Fallback auf Minimal-Insert
            try {
                jdbcTemplate.update(
                        "INSERT INTO mt_medical_staff (id, user_id, display_name) VALUES (?,?,?)",
                        staffId, userId, displayName
                );
                return staffId;
            } catch (Exception ignored2) {
                return null;
            }
        }
    }

    /**
     * Ermittelt lesbaren Anzeigenamen für Patienten.
     */
    private String resolvePatientLabel(String patientId) {
        if (patientId == null || patientId.isBlank()) return "";
        if (jdbcTemplate == null) return patientId;

        try {
            return jdbcTemplate.query(
                    "SELECT name, email FROM mt_user WHERE id = ?",
                    rs -> {
                        if (!rs.next()) return patientId;
                        String name = rs.getString("name");
                        String email = rs.getString("email");
                        if (name != null && !name.isBlank()) return name;
                        if (email != null && !email.isBlank()) return email;
                        return patientId;
                    },
                    patientId
            );
        } catch (Exception ignored) {
            return patientId;
        }
    }
}

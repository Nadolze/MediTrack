package com.meditrack.medication.api;

import com.meditrack.medication.application.MedicationPlanService;
import com.meditrack.medication.application.dto.CreateMedicationPlanCommand;
import com.meditrack.shared.api.SessionKeys;
import com.meditrack.shared.valueobject.UserSession;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/medication/plans")
public class MedicationPlanController {

    private final MedicationPlanService medicationPlanService;

    @Autowired(required = false)
    private JdbcTemplate jdbcTemplate;

    public record PatientOption(String id, String label) {}

    public MedicationPlanController(MedicationPlanService medicationPlanService) {
        this.medicationPlanService = medicationPlanService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String patientId,
                       HttpSession session,
                       Model model) {

        UserSession user = (UserSession) session.getAttribute(SessionKeys.LOGGED_IN_USER);
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("currentUser", user);
        boolean canCreatePlan = user.hasAnyRole("STAFF", "ADMIN");
        model.addAttribute("canCreatePlan", canCreatePlan);

        String effectivePatientId;
        if (user.hasRole("PATIENT")) {
            // Patient sieht nur seine eigenen Pläne
            effectivePatientId = user.getUserId();
        } else {
            // STAFF/ADMIN: Patient wird über Query-Param gewählt
            effectivePatientId = (patientId == null) ? "" : patientId.trim();
        }

        model.addAttribute("patientId", effectivePatientId);

        // NEU: Patient-Label anzeigen (Name/Email)
        model.addAttribute("patientLabel", resolvePatientLabel(effectivePatientId));

        if (!effectivePatientId.isBlank()) {
            model.addAttribute("plans", medicationPlanService.getPlansForPatient(effectivePatientId));
        } else {
            model.addAttribute("plans", List.of());
        }

        return "medication/plan-list";
    }

    @GetMapping("/new")
    public String newPlan(@RequestParam(required = false) String patientId,
                          HttpSession session,
                          Model model) {

        UserSession user = (UserSession) session.getAttribute(SessionKeys.LOGGED_IN_USER);
        if (user == null) {
            return "redirect:/login";
        }

        // PATIENT darf keine Pläne anlegen
        if (user.hasRole("PATIENT")) {
            return "redirect:/medication/plans";
        }

        model.addAttribute("currentUser", user);
        model.addAttribute("canCreatePlan", user.hasAnyRole("STAFF", "ADMIN"));

        List<PatientOption> patients = loadPatientsIfPossible();
        model.addAttribute("patients", patients);

        String effectivePatientId = (patientId == null) ? "" : patientId.trim();
        if (effectivePatientId.isBlank() && !patients.isEmpty()) {
            effectivePatientId = patients.get(0).id(); // Java-kompatibel
        }

        CreateMedicationPlanCommand command = new CreateMedicationPlanCommand();
        command.setPatientId(effectivePatientId);

        model.addAttribute("command", command);
        return "medication/plan-form";
    }

    @PostMapping
    public String create(@ModelAttribute("command") CreateMedicationPlanCommand command,
                         HttpSession session,
                         Model model) {

        UserSession user = (UserSession) session.getAttribute(SessionKeys.LOGGED_IN_USER);
        if (user == null) {
            return "redirect:/login";
        }

        // PATIENT darf keine Pläne anlegen
        if (user.hasRole("PATIENT")) {
            return "redirect:/medication/plans";
        }

        if (command.getPatientId() == null || command.getPatientId().isBlank()) {
            model.addAttribute("currentUser", user);
            model.addAttribute("canCreatePlan", user.hasAnyRole("STAFF", "ADMIN"));
            model.addAttribute("patients", loadPatientsIfPossible());
            model.addAttribute("error", "Bitte einen Patienten auswählen, bevor der Plan angelegt wird.");
            return "medication/plan-form";
        }

        // FK mt_medication_plan.patient_id -> mt_patient.id
        ensurePatientRowExists(command.getPatientId());

        medicationPlanService.createPlan(user, command);
        return "redirect:/medication/plans?patientId=" + command.getPatientId();
    }

    /**
     * Dropdown-Quelle für STAFF/ADMIN: mt_user (role=PATIENT).
     */
    private List<PatientOption> loadPatientsIfPossible() {
        if (jdbcTemplate == null) {
            return Collections.emptyList();
        }
        try {
            return jdbcTemplate.query(
                    "SELECT id, name, email FROM mt_user WHERE role = 'PATIENT' ORDER BY name",
                    (rs, rowNum) -> {
                        String id = rs.getString("id");
                        String name = rs.getString("name");
                        String email = rs.getString("email");
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
     * Erzeugt einen minimalen mt_patient-Datensatz, falls er für die UserId noch nicht existiert.
     * Wir setzen mt_patient.id = userId, damit "patientId = userId" konsistent bleibt.
     */
    private void ensurePatientRowExists(String userId) {
        if (jdbcTemplate == null || userId == null || userId.isBlank()) {
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

            String name = null;
            try {
                name = jdbcTemplate.queryForObject(
                        "SELECT name FROM mt_user WHERE id = ?",
                        String.class,
                        userId
                );
            } catch (Exception ignored) {
            }
            if (name == null) {
                name = userId;
            }

            jdbcTemplate.update(
                    "INSERT INTO mt_patient (id, user_id, first_name, last_name) VALUES (?, ?, ?, ?)",
                    userId,
                    userId,
                    name,
                    ""
            );
        } catch (Exception ignored) {
        }
    }

    /**
     * NEU: Label für Anzeige im UI: bevorzugt mt_user.name, dann mt_user.email, sonst ID.
     */
    private String resolvePatientLabel(String patientId) {
        if (patientId == null || patientId.isBlank()) {
            return "";
        }
        if (jdbcTemplate == null) {
            return patientId;
        }
        try {
            return jdbcTemplate.query(
                    "SELECT name, email FROM mt_user WHERE id = ?",
                    rs -> {
                        if (!rs.next()) {
                            return patientId;
                        }
                        String name = rs.getString("name");
                        String email = rs.getString("email");

                        if (name != null && !name.isBlank()) {
                            return name;
                        }
                        if (email != null && !email.isBlank()) {
                            return email;
                        }
                        return patientId;
                    },
                    patientId
            );
        } catch (Exception ignored) {
            return patientId;
        }
    }
}

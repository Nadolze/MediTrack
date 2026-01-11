package com.meditrack.alerts.api;

import com.meditrack.alerts.application.dto.AlertSummaryDto;
import com.meditrack.alerts.application.service.AlertService;
import com.meditrack.shared.api.SessionKeys;
import com.meditrack.shared.valueobject.UserSession;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.List;

/**
 * MVC-Controller für die Alert-Übersichtsseite (Thymeleaf).
 *
 * Verhalten:
 * - PATIENT sieht automatisch seine eigenen Alerts
 * - STAFF/ADMIN kann einen Patienten auswaehlen (patientId RequestParam)
 */
@Controller
public class AlertsPageController {

    private final AlertService alertService;

    // Optionaler JdbcTemplate für einfache DB-Zugriffe.
    @Autowired(required = false)
    private JdbcTemplate jdbcTemplate;

    public AlertsPageController(AlertService alertService) {
        this.alertService = alertService;
    }

    // DTO für Patientenauswahl im UI.
    public record PatientOption(String id, String label) {}

    /**
     * Rendert die Alert-Liste.
     * Patientenkontext wird abhängig von der Rolle bestimmt.
     */
    @GetMapping("/alerts")
    public String list(
            @RequestParam(value = "patientId", required = false) String patientIdParam,
            HttpSession session,
            Model model
    ) {
        UserSession user = (UserSession) session.getAttribute(SessionKeys.LOGGED_IN_USER);
        if (user == null) {
            return "redirect:/login";
        }

        boolean canSelectPatient = user.hasAnyRole("ADMIN", "STAFF");

        // Bestimme effektiven Patientenkontext
        String effectivePatientId;
        if (canSelectPatient) {
            effectivePatientId = (patientIdParam == null) ? "" : patientIdParam.trim();
            model.addAttribute("patients", loadPatientsIfPossible());
        } else {
            effectivePatientId = user.getUserId();
        }

        // Alerts nur laden, wenn ein Patient gewählt ist
        List<AlertSummaryDto> alerts = effectivePatientId.isBlank()
                ? List.of()
                : alertService.listForPatient(effectivePatientId);

        model.addAttribute("user", user.getUsername());
        model.addAttribute("alerts", alerts);
        model.addAttribute("canSelectPatient", canSelectPatient);
        model.addAttribute("selectedPatientId", effectivePatientId);

        return "alerts/alert-list";
    }

    /**
     * Lädt Patienten für die Auswahl (STAFF/ADMIN).
     * Rein für UI-Zwecke, fachlich unkritisch.
     */
    private List<PatientOption> loadPatientsIfPossible() {
        if (jdbcTemplate == null) {
            return Collections.emptyList();
        }

        try {
            return jdbcTemplate.query(
                    "SELECT id, name FROM mt_user WHERE role = 'PATIENT' ORDER BY name",
                    (rs, rowNum) -> new PatientOption(rs.getString("id"), rs.getString("name"))
            );
        } catch (Exception ignored) {
            return Collections.emptyList();
        }
    }
}

package com.meditrack.assignment.api;

import com.meditrack.assignment.application.dto.CreateAssignmentCommand;
import com.meditrack.assignment.application.service.AssignmentService;
import com.meditrack.assignment.domain.valueobject.AssignmentRole;
import com.meditrack.shared.api.SessionKeys;
import com.meditrack.shared.valueobject.UserSession;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.List;

/**
 * BC assignment:
 * - STAFF/ADMIN können medizinisches Personal Patienten zuweisen.
 * - PATIENT sieht (vorerst) nur seine eigenen Zuweisungen.
 */
@Controller
public class AssignmentController {

    private final AssignmentService assignmentService;

    @Autowired(required = false)
    private JdbcTemplate jdbcTemplate;

    public record PatientOption(String id, String label) {}
    public record StaffOption(String id, String label) {}

    public AssignmentController(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @GetMapping("/assignment")
    public String list(
            @RequestParam(name = "patientId", required = false) String patientId,
            HttpSession session,
            Model model
    ) {
        UserSession user = currentUser(session);
        if (user == null) {
            return "redirect:/login";
        }

        boolean canManage = user.hasAnyRole("STAFF", "ADMIN");
        model.addAttribute("currentUser", user);
        model.addAttribute("canManageAssignments", canManage);
        model.addAttribute("roles", AssignmentRole.values());

        String effectivePatientId;
        if (canManage) {
            // Patient-Kontext merken, damit man nicht jedes Mal den Param setzen muss
            effectivePatientId = firstNonBlank(patientId, (String) session.getAttribute(SessionKeys.ACTIVE_PATIENT_ID));
            if (effectivePatientId != null && !effectivePatientId.isBlank()) {
                session.setAttribute(SessionKeys.ACTIVE_PATIENT_ID, effectivePatientId);
            }
            model.addAttribute("patients", loadPatientsIfPossible());
            model.addAttribute("staff", loadStaffIfPossible());
        } else {
            effectivePatientId = user.getUserId();
        }

        model.addAttribute("patientId", effectivePatientId);
        model.addAttribute("assignments", assignmentService.listActiveForPatient(effectivePatientId));
        return "assignment/assignment-list";
    }

    @PostMapping("/assignment")
    public String create(
            @RequestParam("patientId") String patientId,
            @RequestParam("staffId") String staffId,
            @RequestParam(name = "role", required = false) AssignmentRole role,
            HttpSession session,
            RedirectAttributes ra
    ) {
        UserSession user = currentUser(session);
        if (user == null) {
            return "redirect:/login";
        }
        if (!user.hasAnyRole("STAFF", "ADMIN")) {
            ra.addFlashAttribute("error", "Keine Berechtigung.");
            return "redirect:/assignment";
        }

        try {
            assignmentService.assign(new CreateAssignmentCommand(patientId, staffId, role), user.getUserId());
            ra.addFlashAttribute("success", "Zuweisung erstellt.");
        } catch (Exception ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }

        return "redirect:/assignment?patientId=" + patientId;
    }

    @PostMapping("/assignment/{id}/end")
    public String end(
            @PathVariable("id") String assignmentId,
            @RequestParam(name = "patientId", required = false) String patientId,
            HttpSession session,
            RedirectAttributes ra
    ) {
        UserSession user = currentUser(session);
        if (user == null) {
            return "redirect:/login";
        }
        if (!user.hasAnyRole("STAFF", "ADMIN")) {
            ra.addFlashAttribute("error", "Keine Berechtigung.");
            return "redirect:/assignment";
        }

        try {
            assignmentService.end(assignmentId, user.getUserId());
            ra.addFlashAttribute("success", "Zuweisung beendet.");
        } catch (Exception ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }

        String redirectPatientId = firstNonBlank(patientId, (String) session.getAttribute(SessionKeys.ACTIVE_PATIENT_ID));
        if (redirectPatientId == null || redirectPatientId.isBlank()) {
            return "redirect:/assignment";
        }
        return "redirect:/assignment?patientId=" + redirectPatientId;
    }

    private UserSession currentUser(HttpSession session) {
        if (session == null) return null;
        Object v = session.getAttribute(SessionKeys.LOGGED_IN_USER);
        return (v instanceof UserSession u) ? u : null;
    }

    private List<PatientOption> loadPatientsIfPossible() {
        if (jdbcTemplate == null) return Collections.emptyList();

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

    private List<StaffOption> loadStaffIfPossible() {
        if (jdbcTemplate == null) return Collections.emptyList();

        try {
            // mt_medical_staff ist der FK-Partner. Wir holen display_name + fallback aus mt_user.
            return jdbcTemplate.query(
                    "SELECT s.id, s.display_name, u.email " +
                            "FROM mt_medical_staff s " +
                            "JOIN mt_user u ON u.id = s.user_id " +
                            "WHERE u.role IN ('STAFF','ADMIN') " +
                            "ORDER BY s.display_name",
                    (rs, rowNum) -> {
                        String id = rs.getString("id");
                        String display = rs.getString("display_name");
                        String email = rs.getString("email");
                        String label = (display != null && !display.isBlank())
                                ? display
                                : (email != null && !email.isBlank() ? email : id);
                        return new StaffOption(id, label);
                    }
            );
        } catch (Exception ignored) {
            return Collections.emptyList();
        }
    }

    private String firstNonBlank(String a, String b) {
        if (a != null && !a.isBlank()) return a;
        if (b != null && !b.isBlank()) return b;
        return null;
    }
}

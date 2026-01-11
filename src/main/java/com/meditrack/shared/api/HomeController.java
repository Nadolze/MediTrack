package com.meditrack.shared.api;

import com.meditrack.shared.valueobject.UserSession;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collections;
import java.util.List;

/**
 * Zuständig für Landing- und Home-Seite.
 */
@Controller
public class HomeController {

    // Optionaler JdbcTemplate für einfache DB-Zugriffe.
    @Autowired(required = false)
    private JdbcTemplate jdbcTemplate;

    // DTO für Patientenauswahl im UI.
    public record PatientOption(String id, String label) {}

    @GetMapping("/")
    public String root(HttpSession session, Model model) {
        return renderLandingOrHome(session, model);
    }

    @GetMapping("/home")
    public String home(HttpSession session, Model model) {
        return renderLandingOrHome(session, model);
    }

    /**
     * Zentrale Render-Logik für Landing vs. Home.
     */
    private String renderLandingOrHome(HttpSession session, Model model) {
        UserSession user = getCurrentUser(session);

        if (user == null) {
            return "user/landing";
        }

        model.addAttribute("currentUser", user);

        if (user.hasRole("PATIENT")) {
            // Patient sieht nur seine eigenen Pläne (patientId = userId)
            model.addAttribute("patientId", user.getUserId());
        } else {
            // STAFF/ADMIN: Dropdown mit allen Patienten aus mt_user
            model.addAttribute("patients", loadPatientsIfPossible());
        }

        model.addAttribute("canCreatePlan", user.hasAnyRole("STAFF", "ADMIN"));
        return "user/home";
    }

    /**
     * Für STAFF/ADMIN (Dropdown).
     * Quelle ist mt_user, da mt_patient aktuell leer ist (wird im SQL-Dump gelöscht).
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
     * Hilfsmethode zum sicheren Zugriff auf den eingeloggten Benutzer.
     */
    private UserSession getCurrentUser(HttpSession session) {
        if (session == null) {
            return null;
        }
        Object value = session.getAttribute(SessionKeys.LOGGED_IN_USER);
        return (value instanceof UserSession u) ? u : null;
    }
}

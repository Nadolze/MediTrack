package com.meditrack.alerts.api;

import com.meditrack.shared.api.SessionKeys;
import com.meditrack.shared.valueobject.UserSession;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Web-/MVC-Test für die Alert-Übersichtsseite.
 *
 * Ziel:
 * - Absicherung des Zugriffsverhaltens auf /alerts
 * - Prüfung der Session-basierten Authentifizierung
 *
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AlertsControllerWebTest {

    @Autowired
    private MockMvc mockMvc;

    private static UserSession patient() {
        return new UserSession("p1", "Max Patient", "patient@example.com", "PATIENT");
    }

    @Test
    void redirectsToLoginWhenNotLoggedIn() throws Exception {
        mockMvc.perform(get("/alerts"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void listRendersForPatient() throws Exception {
        mockMvc.perform(get("/alerts")
                        .sessionAttr(SessionKeys.LOGGED_IN_USER, patient()))
                .andExpect(status().isOk());
        // Optional (nur wenn du sicher den View-Namen kennst):
        // .andExpect(view().name("alerts/alert-list"));
    }
}

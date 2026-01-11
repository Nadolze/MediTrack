package com.meditrack.medication.infrastructure;

import com.meditrack.shared.api.HomeController;
import com.meditrack.shared.api.SessionKeys;
import com.meditrack.shared.valueobject.UserSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests für die Rollenlogik auf der Home-Seite.
 *
 * Ziel:
 * - Sicherstellen, dass der HomeController korrekt nach Benutzerrolle reagiert
 * - Trennung zwischen PATIENT- und STAFF-Sicht verifizieren
 *
 * Hinweis: Der Controller nutzt optional ein JdbcTemplate. In diesem Unit-Test
 * wird es nicht injiziert (bleibt null), aber die Seite soll trotzdem rendern.
 */
class HomeControllerRoleTest {

    @Test
    @DisplayName("PATIENT sieht patientId und kann keinen Plan erstellen")
    void home_shouldExposePatientIdWhenUserIsPatient() throws Exception {
        HomeController controller = new HomeController();
        MockMvc mvc = MockMvcBuilders.standaloneSetup(controller).build();

        UserSession session = new UserSession(
                "patient-123",
                "MT1",
                "meditrack1@wolfdel.eu",
                "PATIENT"
        );

        mvc.perform(get("/").sessionAttr(SessionKeys.LOGGED_IN_USER, session))
                .andExpect(status().isOk())
                .andExpect(view().name("user/home"))
                .andExpect(model().attribute("patientId", "patient-123"))
                .andExpect(model().attribute("canCreatePlan", false))
                .andExpect(model().attribute("currentUser", session));
    }

    @Test
    @DisplayName("STAFF sieht Patientenliste (auch wenn JdbcTemplate in Unit-Test null ist)")
    void home_shouldExposePatientsListForStaffEvenWithoutJdbcTemplate() throws Exception {
        HomeController controller = new HomeController();
        MockMvc mvc = MockMvcBuilders.standaloneSetup(controller).build();

        UserSession session = new UserSession(
                "staff-1",
                "timoST",
                "timoST@meditrack.com",
                "STAFF"
        );

        mvc.perform(get("/").sessionAttr(SessionKeys.LOGGED_IN_USER, session))
                .andExpect(status().isOk())
                .andExpect(view().name("user/home"))
                .andExpect(model().attributeExists("patients"))
                .andExpect(model().attribute("canCreatePlan", true))
                .andExpect(model().attribute("currentUser", session));
    }
}

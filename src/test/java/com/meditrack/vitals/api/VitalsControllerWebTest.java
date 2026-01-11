package com.meditrack.vitals.api;

import com.meditrack.shared.api.SessionKeys;
import com.meditrack.shared.valueobject.UserSession;
import com.meditrack.vitals.domain.repository.VitalReadingRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
/**
 * WebMvcTest für den VitalsController.
 *
 * Ziel:
 * - Prüfen, dass unautorisierte Zugriffe auf VitalReadings auf /login weitergeleitet werden
 * - Sicherstellen, dass Patienten die Reading-Liste sehen können
 * - Sicherstellen, dass das Formular für neue VitalReadings für Patienten korrekt gerendert wird
 *
 */
@WebMvcTest(VitalsController.class)
class VitalsControllerWebTest {

    @Autowired MockMvc mvc;

    @MockBean VitalReadingRepository vitalReadingRepository;

    @Test
    void redirectsToLoginWhenNotLoggedIn() throws Exception {
        mvc.perform(get("/vitals/readings"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void listRendersForPatient() throws Exception {
        // UserSession mocken, damit du keinen konkreten Konstruktor kennen musst
        UserSession user = Mockito.mock(UserSession.class);
        when(user.getUserId()).thenReturn("patient-1");
        when(user.hasAnyRole("ADMIN", "STAFF")).thenReturn(false);

        when(vitalReadingRepository.findByPatientIdOrderByMeasuredAtDesc("patient-1"))
                .thenReturn(List.of());

        mvc.perform(get("/vitals/readings")
                        .sessionAttr(SessionKeys.LOGGED_IN_USER, user))
                .andExpect(status().isOk())
                .andExpect(view().name("vitals/reading-list"))
                .andExpect(model().attributeExists("readings"));
    }

    @Test
    void formRendersForPatient() throws Exception {
        UserSession user = Mockito.mock(UserSession.class);
        when(user.getUserId()).thenReturn("patient-1");
        when(user.hasAnyRole("ADMIN", "STAFF")).thenReturn(false);

        mvc.perform(get("/vitals/readings/new")
                        .sessionAttr(SessionKeys.LOGGED_IN_USER, user))
                .andExpect(status().isOk())
                .andExpect(view().name("vitals/reading-form"))
                .andExpect(model().attributeExists("types"))
                .andExpect(model().attributeExists("units"));
    }
}

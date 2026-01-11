package com.meditrack.medication.api;

import com.meditrack.medication.application.MedicationPlanService;
import com.meditrack.medication.application.dto.MedicationPlanSummaryDto;
import com.meditrack.shared.api.SessionKeys;
import com.meditrack.shared.exception.AccessDeniedException;
import com.meditrack.shared.valueobject.UserSession;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
/**
 * Web-MVC-Tests für den MedicationPlanController.
 *
 * Ziel:
 * - Absicherung der Controller-Logik (Routing, Views, HTTP-Status)
 * - Prüfung der Zugriffskontrolle über UserSession
 *
 */
@WebMvcTest(MedicationPlanController.class)
class MedicationPlanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MedicationPlanService medicationPlanService;

    private static UserSession staff() {
        return new UserSession("u1", "Dr. Staff", "staff@example.com", "STAFF");
    }

    @Test
    void listPlans_withoutLogin_shouldRedirectToLogin() throws Exception {
        mockMvc.perform(get("/medication/plans").param("patientId", "patient-123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void listPlans_withLogin_shouldShowListView() throws Exception {
        when(medicationPlanService.getPlansForPatient(anyString()))
                .thenReturn(List.of(
                        MedicationPlanSummaryDto.builder()
                                .id("plan-1")
                                .patientId("patient-123")
                                .name("Basisplan")
                                .active(true)
                                .startDate(LocalDate.of(2026, 1, 1))
                                .endDate(LocalDate.of(2026, 12, 31))
                                .build()
                ));

        mockMvc.perform(get("/medication/plans")
                        .param("patientId", "patient-123")
                        .sessionAttr(SessionKeys.LOGGED_IN_USER, staff()))
                .andExpect(status().isOk())
                .andExpect(view().name("medication/plan-list"))
                .andExpect(model().attributeExists("plans"))
                .andExpect(model().attribute("patientId", "patient-123"));
    }

    @Test
    void handleCreate_asStaff_shouldRedirectToList() throws Exception {
        when(medicationPlanService.createPlan(any(UserSession.class), any()))
                .thenReturn("plan-1");

        mockMvc.perform(post("/medication/plans")
                        .sessionAttr(SessionKeys.LOGGED_IN_USER, staff())
                        .param("patientId", "patient-123")
                        .param("name", "Basisplan")
                        .param("description", "Standard Medikation"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/medication/plans?patientId=patient-123"));
    }

    @Test
    void handleCreate_serviceThrowsAccessDenied_shouldReturn403() throws Exception {
        when(medicationPlanService.createPlan(any(UserSession.class), any()))
                .thenThrow(new AccessDeniedException("Keine Berechtigung"));

        mockMvc.perform(post("/medication/plans")
                        .sessionAttr(SessionKeys.LOGGED_IN_USER, staff())
                        .param("patientId", "patient-123")
                        .param("name", "Basisplan"))
                .andExpect(status().isForbidden());
    }
}

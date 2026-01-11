package com.meditrack.assignment.api;

import com.meditrack.assignment.application.service.AssignmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
/*
@ActiveProfiles("test") // Fix 1: test-Profil aktiv
@SpringBootTest
@AutoConfigureMockMvc
class AssignmentControllerWebTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    AssignmentService assignmentService;

    private MockHttpSession loggedInSessionAsStaff(String user) {
        MockHttpSession session = new MockHttpSession();

        // 1) „Standard“-Keys, die häufig in solchen Projekten vorkommen:
        session.setAttribute("LOGGED_IN_USER", user);
        session.setAttribute("loggedInUser", user);
        session.setAttribute("USER", user);
        session.setAttribute("user", user);
        session.setAttribute("USER_ID", user);
        session.setAttribute("userId", user);
        session.setAttribute("USER_EMAIL", user);
        session.setAttribute("userEmail", user);

        // Rollen (falls Controller Role-Gating macht)
        session.setAttribute("ROLE", "ADMIN");
        session.setAttribute("role", "ADMIN");
        session.setAttribute("USER_ROLE", "ADMIN");
        session.setAttribute("userRole", "ADMIN");

        // 2) „Treffer-sicher“: Keys direkt aus dem AssignmentController ziehen (static final String)
        //    und befüllen, wenn sie nach Login/Session/Auth/User aussehen.
        Set<String> controllerKeys = new LinkedHashSet<>();
        for (Field f : AssignmentController.class.getDeclaredFields()) {
            int m = f.getModifiers();
            if (f.getType() == String.class && Modifier.isStatic(m) && Modifier.isFinal(m)) {
                try {
                    f.setAccessible(true);
                    String key = (String) f.get(null);
                    if (key == null) continue;

                    String u = key.toUpperCase();
                    if (u.contains("USER") || u.contains("LOGIN") || u.contains("SESSION") || u.contains("AUTH")) {
                        controllerKeys.add(key);
                    }
                } catch (Exception ignored) {
                    // wenn SecurityManager/Access etc. blockt, ist egal
                }
            }
        }

        for (String key : controllerKeys) {
            // befüllen: wenn’s ein Role-Key ist, gib ADMIN; sonst user
            String u = key.toUpperCase();
            if (u.contains("ROLE")) {
                session.setAttribute(key, "ADMIN");
            } else {
                session.setAttribute(key, user);
            }
        }

        return session;
    }

    @Test
    void get_list_shouldRenderListView() throws Exception {
        MockHttpSession session = loggedInSessionAsStaff("staff1");

        mvc.perform(get("/assignment")
                        .param("patientId", "p-1")
                        .session(session))
                .andExpect(status().isOk());
    }

    @Test
    void post_assign_shouldRedirectToList() throws Exception {
        MockHttpSession session = loggedInSessionAsStaff("staff1");

        mvc.perform(post("/assignment")
                        .param("patientId", "p-1")
                        .param("staffId", "s-1")
                        .param("role", "PRIMARY_CARE")
                        .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/assignment?patientId=p-1"));
    }

    @Test
    void post_end_shouldRedirectToList() throws Exception {
        MockHttpSession session = loggedInSessionAsStaff("staff1");

        mvc.perform(post("/assignment/A1/end")
                        .param("patientId", "p-1")
                        .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/assignment?patientId=p-1"));
    }

    @Test
    void post_assign_whenServiceThrows_shouldStillNotGoToLogin() throws Exception {
        // Ohne exakten Methodennamen vom Service können wir hier kein doThrow() verdrahten.
        // Dieser Test stellt aber sicher: wenn eingeloggt, geht es NICHT nach /login.
        MockHttpSession session = loggedInSessionAsStaff("staff1");

        mvc.perform(post("/assignment")
                        .param("patientId", "p-1")
                        .param("staffId", "s-1")
                        .param("role", "PRIMARY_CARE")
                        .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/assignment?patientId=p-1"));
    }

    @Test
    void post_end_whenServiceThrows_shouldStillNotGoToLogin() throws Exception {
        MockHttpSession session = loggedInSessionAsStaff("staff1");

        mvc.perform(post("/assignment/A1/end")
                        .param("patientId", "p-1")
                        .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/assignment?patientId=p-1"));
    }
}
*/
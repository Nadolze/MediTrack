package com.meditrack.user.api;

import com.meditrack.shared.api.SessionKeys;
import com.meditrack.shared.valueobject.UserSession;
import com.meditrack.user.application.service.UserApplicationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
/**
 * Unit-/WebMvc-Tests für den UserController.
 *
 * Ziel:
 * - Prüfen, dass GET /login das Login-Formular liefert
 * - Prüfen, dass POST /login bei gültigen Credentials die Session setzt und auf /home redirectet
 * - Prüfen, dass POST /login bei falschen Credentials die Login-View mit Fehler zeigt
 * - Prüfen, dass POST /logout die Session invalidiert und zur Landing-Page redirectet
 */
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserApplicationService userApplicationService;

    @Test
    @DisplayName("GET /login liefert Login-View")
    void showLoginForm_shouldReturnLoginView() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/login"))
                .andExpect(model().attributeExists("login"));
    }

    @Test
    @DisplayName("POST /login bei Erfolg setzt Session-Principal und redirectet auf /home")
    void handleLogin_withValidCredentials_shouldSetSessionAndRedirectToHome() throws Exception {
        UserSession sessionUser = new UserSession("123", "john", "john@example.com", "PATIENT");
        when(userApplicationService.authenticate("john", "secret123")).thenReturn(Optional.of(sessionUser));

        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(post("/login")
                        .session(session)
                        .param("usernameOrEmail", "john")
                        .param("password", "secret123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));

        Object loggedIn = session.getAttribute(SessionKeys.LOGGED_IN_USER);
        assertThat(loggedIn).isInstanceOf(UserSession.class);
        assertThat(((UserSession) loggedIn).getUsername()).isEqualTo("john");

        verify(userApplicationService, times(1)).authenticate("john", "secret123");
    }

    @Test
    @DisplayName("POST /login bei Fehler zeigt Login-View + error")
    void handleLogin_withInvalidCredentials_shouldShowLoginWithError() throws Exception {
        when(userApplicationService.authenticate("john", "wrong")).thenReturn(Optional.empty());

        mockMvc.perform(post("/login")
                        .param("usernameOrEmail", "john")
                        .param("password", "wrong"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/login"))
                .andExpect(model().attributeExists("error"));

        verify(userApplicationService, times(1)).authenticate("john", "wrong");
    }

    @Test
    @DisplayName("POST /logout invalidiert Session und leitet zur Landing-Page (/)")
    void handleLogout_shouldInvalidateSessionAndRedirectToRoot() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(SessionKeys.LOGGED_IN_USER, new UserSession("1", "john", "john@example.com", "PATIENT"));

        mockMvc.perform(post("/logout").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        assertThat(session.isInvalid()).isTrue();
    }
}

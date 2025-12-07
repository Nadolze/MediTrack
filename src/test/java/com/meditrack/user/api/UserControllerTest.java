package com.meditrack.user.api;

import com.meditrack.user.application.dto.UserLoginDto;
import com.meditrack.user.application.dto.UserRegistrationDto;
import com.meditrack.user.application.service.UserApplicationService;
import com.meditrack.user.domain.entity.User;
import com.meditrack.user.domain.valueobject.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Web-MVC-Tests für den UserController.
 *
 * Getestet werden:
 *  - Mappings / Views für Login & Registrierung
 *  - Aufruf des Application-Services
 *  - grundlegende Validierung und Fehlermeldungen
 */
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserApplicationService userApplicationService;

    @Test
    @DisplayName("GET /register liefert Registrierungsformular")
    void showRegisterForm_shouldReturnRegisterViewWithEmptyModel() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/register"))
                .andExpect(model().attributeExists("registration"));
    }

    @Test
    @DisplayName("POST /register mit gültigen Daten leitet auf /login um")
    void handleRegister_validData_shouldRedirectToLogin() throws Exception {
        User dummyUser = new User(UserId.generate(), "marcell", "marcell@example.com");
        Mockito.when(userApplicationService.registerUser(any(UserRegistrationDto.class)))
                .thenReturn(dummyUser);

        mockMvc.perform(post("/register")
                        .param("username", "marcell")
                        .param("email", "marcell@example.com")
                        .param("password", "geheimespw"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    @DisplayName("POST /register mit ungültigen Daten bleibt im Formular und zeigt Fehler")
    void handleRegister_invalidData_shouldStayOnFormWithErrors() throws Exception {
        mockMvc.perform(post("/register")
                        // ungültige / leere Werte
                        .param("username", " ")
                        .param("email", "keine-mail")
                        .param("password", "123"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/register"))
                .andExpect(model().attributeHasFieldErrors("registration", "username"))
                .andExpect(model().attributeHasFieldErrors("registration", "email"))
                .andExpect(model().attributeHasFieldErrors("registration", "password"));
    }

    @Test
    @DisplayName("GET /login liefert Login-Formular")
    void showLoginForm_shouldReturnLoginViewWithEmptyModel() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/login"))
                .andExpect(model().attributeExists("login"));
    }

    @Test
    @DisplayName("POST /login mit falschen Daten zeigt Fehlermeldung")
    void handleLogin_invalidCredentials_shouldShowError() throws Exception {
        Mockito.when(userApplicationService.login(eq("marcell"), eq("falsch")))
                .thenReturn(false);

        mockMvc.perform(post("/login")
                        .param("usernameOrEmail", "marcell")
                        .param("password", "falsch"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/login"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    @DisplayName("POST /login mit korrekten Daten zeigt Home-View")
    void handleLogin_validCredentials_shouldShowHome() throws Exception {
        Mockito.when(userApplicationService.login(eq("marcell"), eq("richtig")))
                .thenReturn(true);

        mockMvc.perform(post("/login")
                        .param("usernameOrEmail", "marcell")
                        .param("password", "richtig"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attributeExists("message"));
    }
}

package com.meditrack.user.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meditrack.user.api.dto.CreateUserRequest;
import com.meditrack.user.application.service.UserApplicationService;
import com.meditrack.user.domain.entity.User;
import com.meditrack.user.domain.valueobject.UserId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;

/**
 * Testklasse für den REST-Controller UserController.
 *
 * Testet:
 * - HTTP POST /users
 * - korrektes JSON-Mapping
 * - Interaktion mit dem Application Service
 */
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserApplicationService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void sollteBenutzerErstellen() throws Exception {
        // Arrange: Testdaten
        CreateUserRequest request = new CreateUserRequest();
        request.name = "Marcell";
        request.email = "marcell@example.com";

        User user = new User(
                new UserId("123"),
                "Marcell",
                "marcell@example.com"
        );

        // Mock: Was der Service zurückgeben soll
        when(userService.createUser("Marcell", "marcell@example.com"))
                .thenReturn(user);

        // Act + Assert: HTTP-Request simulieren
        mockMvc.perform(post("/users")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("123"))
                .andExpect(jsonPath("$.name").value("Marcell"))
                .andExpect(jsonPath("$.email").value("marcell@example.com"));

        // Prüfen: Service wurde genau 1x aufgerufen
        verify(userService, times(1))
                .createUser("Marcell", "marcell@example.com");
    }
}

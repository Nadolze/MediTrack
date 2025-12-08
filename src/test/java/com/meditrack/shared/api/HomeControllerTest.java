package com.meditrack.shared.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Web-MVC-Tests für den HomeController.
 *
 * Getestet werden:
 *  - Landing-Page ("/")
 *  - einfache Home-Seite ("/home")
 */
@WebMvcTest(HomeController.class)
class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET / liefert Landing-Page")
    void root_shouldReturnLandingView() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("landing"))
                .andExpect(model().attributeExists("title"));
    }

    @Test
    @DisplayName("GET /home liefert Home-View")
    void home_shouldReturnHomeView() throws Exception {
        mockMvc.perform(get("/home"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attributeExists("title"));
    }
}
